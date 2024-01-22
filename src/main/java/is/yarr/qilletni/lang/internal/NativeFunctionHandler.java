package is.yarr.qilletni.lang.internal;

import is.yarr.qilletni.api.lib.BeforeAnyInvocation;
import is.yarr.qilletni.api.lib.NativeOn;
import is.yarr.qilletni.lang.exceptions.NativeMethodNotBoundException;
import is.yarr.qilletni.lang.exceptions.lib.NoNativeLibraryConstructorFoundException;
import is.yarr.qilletni.lang.exceptions.QilletniException;
import is.yarr.qilletni.lang.exceptions.lib.UninjectableConstructorTypeException;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapterInvoker;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.lang.types.TypeUtils;
import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NativeFunctionHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NativeFunctionHandler.class);
    
    private final Map<MethodSignature, InvocableMethod> nativeMethods = new HashMap<>();
    
    private final TypeAdapterInvoker typeAdapterInvoker;
    private final List<Object> injectableInstances;

    public NativeFunctionHandler(TypeAdapterInvoker typeAdapterInvoker) {
        this(typeAdapterInvoker, new ArrayList<>());
    }

    public NativeFunctionHandler(TypeAdapterInvoker typeAdapterInvoker, List<Object> injectableInstances) {
        this.typeAdapterInvoker = typeAdapterInvoker;
        this.injectableInstances = injectableInstances;
    }
    
    public void addInjectableInstance(Object object) {
        injectableInstances.add(object);
    }

    public void registerClasses(Class<?>... nativeMethodClass) {
        for (var clazz : nativeMethodClass) {
            var invokedOn = getNativeOn(clazz.getDeclaredAnnotations());
            var beforeAnyInvocationMethod = getBeforeAnyInvocationMethod(clazz).orElse(null);
            
            for (var declaredMethod : clazz.getDeclaredMethods()) {
                if (hasAnnotation(declaredMethod, BeforeAnyInvocation.class) ||
                        !declaredMethod.accessFlags().contains(AccessFlag.PUBLIC)) {
                    continue;
                }
                
                var methodInvokedOn = invokedOn.or(() -> getNativeOn(declaredMethod.getDeclaredAnnotations()));
                
                var paramCount = declaredMethod.getParameterCount();
//                if (methodInvokedOn.isPresent()) {
//                    paramCount--;
//                }
                
                LOGGER.debug("native func {} params: {}", declaredMethod.getName(), paramCount);
                
                nativeMethods.put(new MethodSignature(declaredMethod.getName(), paramCount, methodInvokedOn.orElse(null)), new InvocableMethod(declaredMethod, beforeAnyInvocationMethod));
            }
        }
    }
    
    private Optional<Method> getBeforeAnyInvocationMethod(Class<?> nativeMethodClass) {
        return Arrays.stream(nativeMethodClass.getDeclaredMethods())
                .filter(method -> hasAnnotation(method, BeforeAnyInvocation.class))
                .findFirst();
    }
    
    private boolean hasAnnotation(Method method, Class<?> annotationClass) {
        return Arrays.stream(method.getDeclaredAnnotations())
                .anyMatch(annotationClass::isInstance);
    }
    
    private Optional<QilletniTypeClass<?>> getNativeOn(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof NativeOn nativeOn) {
                // If the type isn't found, assume it is an entity that isn't known yet
                return Optional.of(TypeUtils.getTypeFromStringOrEntity(nativeOn.value()));
            }
        }
        
        return Optional.empty();
    }
    
    public QilletniType invokeNativeMethod(ParserRuleContext ctx, String name, List<QilletniType> params, int definedParamCount, QilletniTypeClass<?> invokedUponType) {
        LOGGER.debug("invokeNativeMethod({}, {}, {})", name, definedParamCount, invokedUponType);
        var invocableMethod = nativeMethods.get(new MethodSignature(name, definedParamCount, invokedUponType));
        if (invocableMethod == null) {
            throw new NativeMethodNotBoundException(ctx, "Native method not bound to anything!");
        }

        try {
            var instance = createInstanceForMethod(invocableMethod);
            
            if (invocableMethod.beforeAny() != null) {
                typeAdapterInvoker.invokeMethod(instance, invocableMethod.beforeAny(), params);
            }
            
            return typeAdapterInvoker.invokeMethod(instance, invocableMethod.method(), params);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        } catch (QilletniException e) {
            throw new QilletniException(ctx, e.getMessage());
        }
    }
    
    private Object createInstanceForMethod(InvocableMethod invocableMethod) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!(invocableMethod.beforeAny() != null && invocableMethod.beforeAny().accessFlags().contains(AccessFlag.STATIC))
                || !invocableMethod.method().accessFlags().contains(AccessFlag.STATIC)) {
            var declaringClass = invocableMethod.method().getDeclaringClass();
            var constructors = declaringClass.getConstructors();

            if (constructors.length == 0) {
                throw new NoNativeLibraryConstructorFoundException();
            }

            var constructor = constructors[0];

            Arrays.stream(constructor.getParameterTypes()).filter(paramType ->
                    injectableInstances.stream().noneMatch(paramType::isInstance))
                    .findFirst().ifPresent(invalidParam -> {
                        throw new UninjectableConstructorTypeException(String.format("Attempted to inject uninjectable class %s", invalidParam.getCanonicalName()));        
                    });

            var params = Arrays.stream(constructor.getParameterTypes()).map(paramType ->
                    injectableInstances.stream().filter(paramType::isInstance).findFirst().orElse(null))
                    .toArray(Object[]::new);
            
            return constructor.newInstance(params);
        }
        
        return null;
    }
    
    record MethodSignature(String name, int params, QilletniTypeClass<?> nativeOn) {
    }

    /**
     * A set of native methods that may be invoked in Qilletni.
     * 
     * @param method The actual method to invoke and return its value from
     * @param beforeAny A nullable, class-wide method that is invoked before any methods in the class are invoked. This
     *                  is used for setup of objects where many methods in a class have repeating behavior (e.g.
     *                  populating music-related types.  
     */
    record InvocableMethod(Method method, Method beforeAny) {}
}

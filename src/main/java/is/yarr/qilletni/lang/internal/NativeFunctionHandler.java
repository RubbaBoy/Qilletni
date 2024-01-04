package is.yarr.qilletni.lang.internal;

import is.yarr.qilletni.lang.exceptions.NativeMethodNotBoundException;
import is.yarr.qilletni.lang.exceptions.QilletniException;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapterInvoker;
import is.yarr.qilletni.lang.types.QilletniType;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.lang.types.TypeUtils;
import org.antlr.v4.runtime.ParserRuleContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class NativeFunctionHandler {
    
    private final Map<MethodSignature, InvocableMethod> nativeMethods = new HashMap<>();
    
    private final TypeAdapterInvoker typeAdapterInvoker;

    public NativeFunctionHandler(TypeAdapterInvoker typeAdapterInvoker) {
        this.typeAdapterInvoker = typeAdapterInvoker;
    }

    public void registerClasses(Class<?>... nativeMethodClass) {
        for (var clazz : nativeMethodClass) {
            var invokedOn = getNativeOn(clazz.getDeclaredAnnotations());
            var beforeAnyInvocationMethod = getBeforeAnyInvocationMethod(clazz).orElse(null);
            
            for (var declaredMethod : clazz.getDeclaredMethods()) {
                if (hasAnnotation(declaredMethod, BeforeAnyInvocation.class) ||
                        !(declaredMethod.accessFlags().contains(AccessFlag.PUBLIC) &&
                                declaredMethod.accessFlags().contains(AccessFlag.STATIC))) {
                    continue;
                }
                
                var methodInvokedOn = invokedOn.or(() -> getNativeOn(declaredMethod.getDeclaredAnnotations()));
                
                var paramCount = declaredMethod.getParameterCount();
                if (methodInvokedOn.isPresent()) {
                    paramCount--;
                }
                
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
    
    public QilletniType invokeNativeMethod(ParserRuleContext ctx, String name, List<QilletniType> params, QilletniTypeClass<?> invokedUponType) {
        var paramCount = params.size();
        if (invokedUponType != null) {
            paramCount--;
        }

        var invocableMethod = nativeMethods.get(new MethodSignature(name, paramCount, invokedUponType));
        if (invocableMethod == null) {
            throw new NativeMethodNotBoundException(ctx, "Native method not bound to anything!");
        }

        try {
            if (invocableMethod.beforeAny() != null) {
                typeAdapterInvoker.invokeMethod(invocableMethod.beforeAny(), params);
            }
            
            return typeAdapterInvoker.invokeMethod(invocableMethod.method(), params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (QilletniException e) {
            throw new QilletniException(ctx, e.getMessage());
        }
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

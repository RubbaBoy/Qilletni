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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class NativeFunctionHandler {
    
    private final Map<MethodSignature, Method> nativeMethods = new HashMap<>();
    
    private final TypeAdapterInvoker typeAdapterInvoker;

    public NativeFunctionHandler(TypeAdapterInvoker typeAdapterInvoker) {
        this.typeAdapterInvoker = typeAdapterInvoker;
    }

    public void registerClasses(Class<?>... nativeMethodClass) {
        for (var clazz : nativeMethodClass) {
            var invokedOn = getNativeOn(clazz.getDeclaredAnnotations());
            
            for (var declaredMethod : clazz.getDeclaredMethods()) {
                if (!(declaredMethod.accessFlags().contains(AccessFlag.PUBLIC) && declaredMethod.accessFlags().contains(AccessFlag.STATIC))) {
                    continue;
                }
                
                var methodInvokedOn = invokedOn.or(() -> getNativeOn(declaredMethod.getDeclaredAnnotations()));
                
                var paramCount = declaredMethod.getParameterCount();
                if (methodInvokedOn.isPresent()) {
                    paramCount--;
                }
                
                nativeMethods.put(new MethodSignature(declaredMethod.getName(), paramCount, methodInvokedOn.orElse(null)), declaredMethod);
            }
        }
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

        var method = nativeMethods.get(new MethodSignature(name, paramCount, invokedUponType));
        if (method == null) {
            throw new NativeMethodNotBoundException(ctx, "Native method not bound to anything!");
        }

        try {
            return typeAdapterInvoker.invokeMethod(method, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (QilletniException e) {
            throw new QilletniException(ctx, e.getMessage());
        }
    }
    
    record MethodSignature(String name, int params, QilletniTypeClass<?> nativeOn) {
    }
}

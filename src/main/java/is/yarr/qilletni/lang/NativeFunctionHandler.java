package is.yarr.qilletni.lang;

import is.yarr.qilletni.lang.exceptions.NativeMethodNotBoundException;
import is.yarr.qilletni.lang.internal.NativeOn;
import is.yarr.qilletni.lang.types.QilletniType;
import is.yarr.qilletni.lang.types.TypeUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeFunctionHandler {
    
    private final Map<MethodSignature, Method> nativeMethods = new HashMap<>();
    
    public void init(Class<?>... nativeMethodClass) {
        for (var clazz : nativeMethodClass) {
            for (var declaredMethod : clazz.getDeclaredMethods()) {
                var annotations = declaredMethod.getDeclaredAnnotations();
                Class<? extends QilletniType> invokedOn = null;
                for (Annotation annotation : annotations) {
                    if (annotation instanceof NativeOn nativeOn) {
                        invokedOn = TypeUtils.getTypeFromString(nativeOn.value());
                    }
                }
                
                var paramCount = declaredMethod.getParameterCount();
                if (invokedOn != null) {
                    paramCount--;
                }
                
                nativeMethods.put(new MethodSignature(declaredMethod.getName(), paramCount, invokedOn), declaredMethod);
            }
        }
    }
    
    public QilletniType invokeNativeMethod(String name, List<QilletniType> params, Class<? extends QilletniType> invokedUponType) {
        var paramCount = params.size();
        if (invokedUponType != null) {
            paramCount--;
        }
        
        var method = nativeMethods.get(new MethodSignature(name, paramCount, invokedUponType));
        if (method == null) {
            throw new NativeMethodNotBoundException("Native method not bound to anything!");
        }

        try {
            return (QilletniType) method.invoke(null, params.toArray(Object[]::new));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    record MethodSignature(String name, int params, Class<? extends QilletniType> nativeOn) {
    }
}

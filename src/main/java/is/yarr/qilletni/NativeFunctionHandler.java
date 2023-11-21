package is.yarr.qilletni;

import is.yarr.qilletni.exceptions.NativeMethodNotBoundException;
import is.yarr.qilletni.types.QilletniType;

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
                nativeMethods.put(new MethodSignature(declaredMethod.getName(), declaredMethod.getParameterCount()), declaredMethod);
            }
        }
    }
    
    public QilletniType invokeNativeMethod(String name, List<QilletniType> params) {
        var method = nativeMethods.get(new MethodSignature(name, params.size()));
        if (method == null) {
            throw new NativeMethodNotBoundException("Native method not bound to anything!");
        }

        try {
            return (QilletniType) method.invoke(null, params.toArray(Object[]::new));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    record MethodSignature(String name, int params) {
    }
}

package is.yarr.qilletni.types;

import is.yarr.qilletni.exceptions.TypeMismatchException;

public class TypeUtils {

    public static <T extends QilletniType> T safelyCast(Object object, Class<T> expectedType) {
        if (!expectedType.isInstance(object)) {
            throw new TypeMismatchException("Expected type " + expectedType.getName() + " but received " + object.getClass().getName());
        }

        return expectedType.cast(object);
    }
    
}

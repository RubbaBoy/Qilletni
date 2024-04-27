package is.yarr.qilletni.api.exceptions;

public class IncompatibleProviderTypeException extends QilletniException {

    public IncompatibleProviderTypeException() {
        super();
    }

    public IncompatibleProviderTypeException(String message) {
        super(message);
    }

    public IncompatibleProviderTypeException(Throwable cause) {
        super(cause);
    }
    
    public static <T> T ensureType(Object obj, Class<T> clazz) {
        if (!clazz.isInstance(obj)) {
            throw new IncompatibleProviderTypeException("Expected " + clazz.getName() + " but got " + obj.getClass().getName());
        }
        
        return clazz.cast(obj);
    }
}

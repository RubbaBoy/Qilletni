package is.yarr.qilletni.lang.exceptions;

public class NoTypeAdapterException extends QilletniException {
    public NoTypeAdapterException(Class<?> to) {
        super("No type adapter for converting to " + to.getCanonicalName());
    }
    
    public NoTypeAdapterException(Class<?> from, Class<?> to) {
        super("No type adapter for converting from " + from.getCanonicalName() + " to " + to.getCanonicalName());
    }
}

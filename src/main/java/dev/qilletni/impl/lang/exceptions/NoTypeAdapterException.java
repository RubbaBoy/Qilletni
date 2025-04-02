package dev.qilletni.impl.lang.exceptions;

public class NoTypeAdapterException extends QilletniContextException {
    public NoTypeAdapterException(Class<?> to) {
        super("No type adapter for converting to " + to.getCanonicalName());
    }
    
    public NoTypeAdapterException(Class<?> from, Class<?> to) {
        super("No type adapter for converting from " + from.getCanonicalName() + " to " + to.getCanonicalName());
    }

    public NoTypeAdapterException(Class<?> from, Class<?> to, String message) {
        super("No type adapter for converting from %s to %s: %s".formatted(from.getCanonicalName(), to.getCanonicalName(), message));
    }
}

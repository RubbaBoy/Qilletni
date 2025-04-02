package dev.qilletni.lib.spotify.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class ThrowableVoid<T> implements Function<Throwable, T> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ThrowableVoid.class);
    
    private final String message;
    private final T returnValue;

    public ThrowableVoid() {
        this("Completable future completed exceptionally");
    }
    
    public ThrowableVoid(String message) {
        this(message, null);
    }
    
    public ThrowableVoid(String message, T returnValue) {
        this.message = message;
        this.returnValue = returnValue;
    }

    @Override
    public T apply(Throwable throwable) {
        LOGGER.error(message, throwable);
        return returnValue;
    }
}

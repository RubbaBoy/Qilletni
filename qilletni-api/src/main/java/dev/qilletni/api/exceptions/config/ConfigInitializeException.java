package dev.qilletni.api.exceptions.config;

public class ConfigInitializeException extends RuntimeException {

    public ConfigInitializeException() {
        super();
    }

    public ConfigInitializeException(String message) {
        super(message);
    }

    public ConfigInitializeException(String message, Throwable cause) {
        super(message, cause);
    }
}

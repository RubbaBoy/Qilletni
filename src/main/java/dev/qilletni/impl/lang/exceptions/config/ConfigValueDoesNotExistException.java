package dev.qilletni.impl.lang.exceptions.config;

public class ConfigValueDoesNotExistException extends RuntimeException {

    public ConfigValueDoesNotExistException() {
        super();
    }

    public ConfigValueDoesNotExistException(String message) {
        super(message);
    }

    public ConfigValueDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}

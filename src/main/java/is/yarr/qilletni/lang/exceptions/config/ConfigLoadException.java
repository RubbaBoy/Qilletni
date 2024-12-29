package is.yarr.qilletni.lang.exceptions.config;

public class ConfigLoadException extends RuntimeException {

    public ConfigLoadException() {
        super();
    }

    public ConfigLoadException(String message) {
        super(message);
    }

    public ConfigLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}

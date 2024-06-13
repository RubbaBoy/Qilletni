package is.yarr.qilletni.lang.docs.exceptions;

public class DocFormatException extends RuntimeException {
    public DocFormatException(String message) {
        super(message);
    }

    public DocFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}

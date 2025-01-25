package is.yarr.qilletni.api.exceptions;

public class QilletniException extends RuntimeException {

    public QilletniException() {
    }

    public QilletniException(String message) {
        super(message);
    }

    public QilletniException(Throwable cause) {
        super(cause);
    }
}

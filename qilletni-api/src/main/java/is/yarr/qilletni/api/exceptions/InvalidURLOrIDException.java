package is.yarr.qilletni.api.exceptions;

public class InvalidURLOrIDException extends RuntimeException {

    public InvalidURLOrIDException() {
    }

    public InvalidURLOrIDException(String message) {
        super(message);
    }
}

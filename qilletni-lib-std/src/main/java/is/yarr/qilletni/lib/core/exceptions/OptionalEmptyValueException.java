package is.yarr.qilletni.lib.core.exceptions;

import is.yarr.qilletni.api.exceptions.QilletniException;

public class OptionalEmptyValueException extends QilletniException {

    public OptionalEmptyValueException() {
        super();
    }

    public OptionalEmptyValueException(String message) {
        super(message);
    }

    public OptionalEmptyValueException(Throwable cause) {
        super(cause);
    }
}

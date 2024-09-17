package is.yarr.qilletni.lib.spotify.exceptions;

import is.yarr.qilletni.api.exceptions.QilletniException;

public class NoSeedSetException extends QilletniException {

    public NoSeedSetException() {
        super();
    }

    public NoSeedSetException(String message) {
        super(message);
    }

    public NoSeedSetException(Throwable cause) {
        super(cause);
    }
}

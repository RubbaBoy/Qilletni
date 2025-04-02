package dev.qilletni.lib.spotify.exceptions;

import dev.qilletni.api.exceptions.QilletniException;

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

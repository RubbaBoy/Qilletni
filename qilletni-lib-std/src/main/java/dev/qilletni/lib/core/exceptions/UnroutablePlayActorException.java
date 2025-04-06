package dev.qilletni.lib.core.exceptions;

import dev.qilletni.api.exceptions.QilletniException;

public class UnroutablePlayActorException extends QilletniException {

    public UnroutablePlayActorException() {
        super();
    }

    public UnroutablePlayActorException(String message) {
        super(message);
    }

    public UnroutablePlayActorException(Throwable cause) {
        super(cause);
    }
}

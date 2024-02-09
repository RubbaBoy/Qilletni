package is.yarr.qilletni.api.exceptions;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

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

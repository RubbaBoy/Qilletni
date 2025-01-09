package is.yarr.qilletni.lang.exceptions;

import is.yarr.qilletni.api.exceptions.QilletniException;
import is.yarr.qilletni.api.lang.types.QilletniType;

public class UnsupportedOperatorException extends QilletniException {
    
    public UnsupportedOperatorException(QilletniType firstType, QilletniType secondType, String operator) {
        super("Unsupported operator '%s' between %s and %s".formatted(operator, firstType.typeName(), secondType.typeName()));
    }
    
    public UnsupportedOperatorException() {
        super();
    }

    public UnsupportedOperatorException(String message) {
        super(message);
    }

    public UnsupportedOperatorException(Throwable cause) {
        super(cause);
    }
}

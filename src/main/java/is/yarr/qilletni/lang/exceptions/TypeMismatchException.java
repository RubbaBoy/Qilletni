package is.yarr.qilletni.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class TypeMismatchException extends QilletniException {

    public TypeMismatchException() {
    }

    public TypeMismatchException(String message) {
        super(message);
    }

    public TypeMismatchException(ParserRuleContext ctx) {
        super(ctx);
    }

    public TypeMismatchException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public TypeMismatchException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

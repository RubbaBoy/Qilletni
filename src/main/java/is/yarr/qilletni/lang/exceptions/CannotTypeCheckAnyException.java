package is.yarr.qilletni.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class CannotTypeCheckAnyException extends QilletniContextException {
    public CannotTypeCheckAnyException() {
        super();
    }

    public CannotTypeCheckAnyException(String message) {
        super(message);
    }

    public CannotTypeCheckAnyException(ParserRuleContext ctx) {
        super(ctx);
    }

    public CannotTypeCheckAnyException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public CannotTypeCheckAnyException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

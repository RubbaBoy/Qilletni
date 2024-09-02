package is.yarr.qilletni.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class InvalidStaticException extends QilletniContextException {

    public InvalidStaticException() {
    }

    public InvalidStaticException(String message) {
        super(message);
    }

    public InvalidStaticException(ParserRuleContext ctx) {
        super(ctx);
    }

    public InvalidStaticException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public InvalidStaticException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

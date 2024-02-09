package is.yarr.qilletni.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class InvalidSyntaxException extends QilletniContextException {

    public InvalidSyntaxException() {
    }

    public InvalidSyntaxException(String message) {
        super(message);
    }

    public InvalidSyntaxException(ParserRuleContext ctx) {
        super(ctx);
    }

    public InvalidSyntaxException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public InvalidSyntaxException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

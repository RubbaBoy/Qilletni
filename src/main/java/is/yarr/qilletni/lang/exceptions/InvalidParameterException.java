package is.yarr.qilletni.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class InvalidParameterException extends QilletniException {

    public InvalidParameterException() {
    }

    public InvalidParameterException(String message) {
        super(message);
    }

    public InvalidParameterException(ParserRuleContext ctx) {
        super(ctx);
    }

    public InvalidParameterException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public InvalidParameterException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

package is.yarr.qilletni.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class InvalidURLException extends QilletniException {

    public InvalidURLException() {
    }

    public InvalidURLException(String message) {
        super(message);
    }

    public InvalidURLException(ParserRuleContext ctx) {
        super(ctx);
    }

    public InvalidURLException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public InvalidURLException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

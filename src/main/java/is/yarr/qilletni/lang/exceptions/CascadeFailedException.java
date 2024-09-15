package is.yarr.qilletni.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class CascadeFailedException extends QilletniContextException {
    public CascadeFailedException() {
        super();
    }

    public CascadeFailedException(String message) {
        super(message);
    }

    public CascadeFailedException(ParserRuleContext ctx) {
        super(ctx, "For some reason, the cascade didn't return an entity.");
    }

    public CascadeFailedException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public CascadeFailedException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

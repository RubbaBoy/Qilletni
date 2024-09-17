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
        super(ctx, "Invalid cascade operation");
    }

    public CascadeFailedException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public CascadeFailedException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

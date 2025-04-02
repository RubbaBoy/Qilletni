package dev.qilletni.impl.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class AlreadyDefinedException extends QilletniContextException {

    public AlreadyDefinedException() {
    }

    public AlreadyDefinedException(String message) {
        super(message);
    }

    public AlreadyDefinedException(ParserRuleContext ctx) {
        super(ctx);
    }

    public AlreadyDefinedException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public AlreadyDefinedException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

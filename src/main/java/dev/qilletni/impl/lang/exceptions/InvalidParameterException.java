package dev.qilletni.impl.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class InvalidParameterException extends QilletniContextException {

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

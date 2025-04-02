package dev.qilletni.impl.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class ListOutOfBoundsException extends QilletniContextException {

    public ListOutOfBoundsException() {
    }

    public ListOutOfBoundsException(String message) {
        super(message);
    }

    public ListOutOfBoundsException(ParserRuleContext ctx) {
        super(ctx);
    }

    public ListOutOfBoundsException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public ListOutOfBoundsException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

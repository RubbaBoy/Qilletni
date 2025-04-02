package dev.qilletni.impl.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class ListTransformerNotFoundException extends QilletniContextException {
    
    public ListTransformerNotFoundException() {
        super();
    }

    public ListTransformerNotFoundException(String message) {
        super(message);
    }

    public ListTransformerNotFoundException(ParserRuleContext ctx) {
        super(ctx);
    }

    public ListTransformerNotFoundException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public ListTransformerNotFoundException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

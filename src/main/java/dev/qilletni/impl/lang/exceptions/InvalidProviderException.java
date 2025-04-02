package dev.qilletni.impl.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class InvalidProviderException extends QilletniContextException {
    
    public InvalidProviderException() {
        super();
    }

    public InvalidProviderException(String message) {
        super(message);
    }

    public InvalidProviderException(ParserRuleContext ctx) {
        super(ctx);
    }

    public InvalidProviderException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public InvalidProviderException(Throwable cause) {
        super(cause);
    }

    public InvalidProviderException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

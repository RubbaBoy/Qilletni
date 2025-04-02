package dev.qilletni.impl.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class InternalLanguageException extends QilletniContextException {

    public InternalLanguageException() {
        super();
    }

    public InternalLanguageException(String message) {
        super(message);
    }

    public InternalLanguageException(ParserRuleContext ctx) {
        super(ctx);
    }

    public InternalLanguageException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public InternalLanguageException(Throwable cause) {
        super(cause);
    }

    public InternalLanguageException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

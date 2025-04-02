package dev.qilletni.impl.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class UnsetJavaReferenceException extends QilletniContextException {
    
    public UnsetJavaReferenceException() {
    }

    public UnsetJavaReferenceException(String message) {
        super(message);
    }

    public UnsetJavaReferenceException(ParserRuleContext ctx) {
        super(ctx);
    }

    public UnsetJavaReferenceException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public UnsetJavaReferenceException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

package dev.qilletni.impl.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class TypeMismatchException extends QilletniContextException {

    public TypeMismatchException() {
    }

    public TypeMismatchException(String message) {
        super(message);
    }

    public TypeMismatchException(ParserRuleContext ctx) {
        super(ctx);
    }

    public TypeMismatchException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public TypeMismatchException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

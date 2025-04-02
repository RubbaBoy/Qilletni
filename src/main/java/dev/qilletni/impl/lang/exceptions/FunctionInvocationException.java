package dev.qilletni.impl.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class FunctionInvocationException extends QilletniContextException {

    public FunctionInvocationException() {
    }

    public FunctionInvocationException(String message) {
        super(message);
    }

    public FunctionInvocationException(ParserRuleContext ctx) {
        super(ctx);
    }

    public FunctionInvocationException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public FunctionInvocationException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

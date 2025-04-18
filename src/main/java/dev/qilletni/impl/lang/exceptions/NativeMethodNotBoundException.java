package dev.qilletni.impl.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class NativeMethodNotBoundException extends QilletniContextException {

    public NativeMethodNotBoundException() {
    }

    public NativeMethodNotBoundException(String message) {
        super(message);
    }

    public NativeMethodNotBoundException(ParserRuleContext ctx) {
        super(ctx);
    }

    public NativeMethodNotBoundException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public NativeMethodNotBoundException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

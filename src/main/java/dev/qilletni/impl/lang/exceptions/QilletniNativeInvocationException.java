package dev.qilletni.impl.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class QilletniNativeInvocationException extends QilletniContextException {

    public QilletniNativeInvocationException() {
        super();
    }

    public QilletniNativeInvocationException(String message) {
        super(message);
    }

    public QilletniNativeInvocationException(ParserRuleContext ctx) {
        super(ctx);
    }

    public QilletniNativeInvocationException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public QilletniNativeInvocationException(Throwable cause) {
        super(cause);
    }

    public QilletniNativeInvocationException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

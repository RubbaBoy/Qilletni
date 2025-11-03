package dev.qilletni.impl.lang.exceptions.music;

import dev.qilletni.impl.lang.exceptions.QilletniContextException;
import org.antlr.v4.runtime.ParserRuleContext;

public class CollectionNotFoundException extends QilletniContextException {

    public CollectionNotFoundException() {
    }

    public CollectionNotFoundException(String message) {
        super(message);
    }

    public CollectionNotFoundException(ParserRuleContext ctx) {
        super(ctx);
    }

    public CollectionNotFoundException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public CollectionNotFoundException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

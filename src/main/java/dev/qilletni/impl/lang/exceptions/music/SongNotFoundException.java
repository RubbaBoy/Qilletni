package dev.qilletni.impl.lang.exceptions.music;

import dev.qilletni.impl.lang.exceptions.QilletniContextException;
import org.antlr.v4.runtime.ParserRuleContext;

public class SongNotFoundException extends QilletniContextException {

    public SongNotFoundException() {
    }

    public SongNotFoundException(String message) {
        super(message);
    }

    public SongNotFoundException(ParserRuleContext ctx) {
        super(ctx);
    }

    public SongNotFoundException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public SongNotFoundException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

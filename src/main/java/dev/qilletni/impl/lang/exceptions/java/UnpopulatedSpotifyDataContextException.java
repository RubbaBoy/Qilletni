package dev.qilletni.impl.lang.exceptions.java;

import dev.qilletni.impl.lang.exceptions.QilletniContextException;
import org.antlr.v4.runtime.ParserRuleContext;

public class UnpopulatedSpotifyDataContextException extends QilletniContextException {

    public UnpopulatedSpotifyDataContextException() {
        super();
    }

    public UnpopulatedSpotifyDataContextException(String message) {
        super(message);
    }

    public UnpopulatedSpotifyDataContextException(ParserRuleContext ctx) {
        super(ctx);
    }

    public UnpopulatedSpotifyDataContextException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public UnpopulatedSpotifyDataContextException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

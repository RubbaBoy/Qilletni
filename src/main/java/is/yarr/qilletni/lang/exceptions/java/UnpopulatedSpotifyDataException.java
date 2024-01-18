package is.yarr.qilletni.lang.exceptions.java;

import is.yarr.qilletni.lang.exceptions.QilletniException;
import org.antlr.v4.runtime.ParserRuleContext;

public class UnpopulatedSpotifyDataException extends QilletniException {

    public UnpopulatedSpotifyDataException() {
        super();
    }

    public UnpopulatedSpotifyDataException(String message) {
        super(message);
    }

    public UnpopulatedSpotifyDataException(ParserRuleContext ctx) {
        super(ctx);
    }

    public UnpopulatedSpotifyDataException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public UnpopulatedSpotifyDataException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

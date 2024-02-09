package is.yarr.qilletni.lang.exceptions.music;

import is.yarr.qilletni.lang.exceptions.QilletniContextException;
import org.antlr.v4.runtime.ParserRuleContext;

public class AlbumNotFoundException extends QilletniContextException {

    public AlbumNotFoundException() {
    }

    public AlbumNotFoundException(String message) {
        super(message);
    }

    public AlbumNotFoundException(ParserRuleContext ctx) {
        super(ctx);
    }

    public AlbumNotFoundException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public AlbumNotFoundException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

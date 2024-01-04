package is.yarr.qilletni.lang.exceptions.music;

import is.yarr.qilletni.lang.exceptions.QilletniException;
import org.antlr.v4.runtime.ParserRuleContext;

public class SongNotFoundException extends QilletniException {

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

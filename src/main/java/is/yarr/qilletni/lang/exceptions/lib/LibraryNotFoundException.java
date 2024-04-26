package is.yarr.qilletni.lang.exceptions.lib;

import is.yarr.qilletni.lang.exceptions.QilletniContextException;
import org.antlr.v4.runtime.ParserRuleContext;

public class LibraryNotFoundException extends QilletniContextException {

    public LibraryNotFoundException() {
        super();
    }

    public LibraryNotFoundException(String message) {
        super(message);
    }

    public LibraryNotFoundException(ParserRuleContext ctx) {
        super(ctx);
    }

    public LibraryNotFoundException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public LibraryNotFoundException(Throwable cause) {
        super(cause);
    }

    public LibraryNotFoundException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

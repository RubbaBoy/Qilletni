package is.yarr.qilletni.lang.exceptions.lib;

import is.yarr.qilletni.lang.exceptions.QilletniContextException;
import org.antlr.v4.runtime.ParserRuleContext;

public class NoNativeLibraryConstructorFoundContextException extends QilletniContextException {
    
    public NoNativeLibraryConstructorFoundContextException() {
        super();
    }

    public NoNativeLibraryConstructorFoundContextException(String message) {
        super(message);
    }

    public NoNativeLibraryConstructorFoundContextException(ParserRuleContext ctx) {
        super(ctx);
    }

    public NoNativeLibraryConstructorFoundContextException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public NoNativeLibraryConstructorFoundContextException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

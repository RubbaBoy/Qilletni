package is.yarr.qilletni.lang.exceptions.lib;

import is.yarr.qilletni.lang.exceptions.QilletniException;
import org.antlr.v4.runtime.ParserRuleContext;

public class NoNativeLibraryConstructorFoundException extends QilletniException {
    
    public NoNativeLibraryConstructorFoundException() {
        super();
    }

    public NoNativeLibraryConstructorFoundException(String message) {
        super(message);
    }

    public NoNativeLibraryConstructorFoundException(ParserRuleContext ctx) {
        super(ctx);
    }

    public NoNativeLibraryConstructorFoundException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public NoNativeLibraryConstructorFoundException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

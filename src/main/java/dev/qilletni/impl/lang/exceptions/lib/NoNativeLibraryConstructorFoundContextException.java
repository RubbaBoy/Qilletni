package dev.qilletni.impl.lang.exceptions.lib;

import dev.qilletni.impl.lang.exceptions.QilletniContextException;
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

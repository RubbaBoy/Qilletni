package is.yarr.qilletni.lang.exceptions.lib;

import is.yarr.qilletni.lang.exceptions.QilletniContextException;
import org.antlr.v4.runtime.ParserRuleContext;

public class UninjectableConstructorTypeContextException extends QilletniContextException {

    public UninjectableConstructorTypeContextException() {
        super();
    }

    public UninjectableConstructorTypeContextException(String message) {
        super(message);
    }

    public UninjectableConstructorTypeContextException(ParserRuleContext ctx) {
        super(ctx);
    }

    public UninjectableConstructorTypeContextException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public UninjectableConstructorTypeContextException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

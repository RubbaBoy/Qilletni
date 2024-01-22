package is.yarr.qilletni.lang.exceptions.lib;

import is.yarr.qilletni.lang.exceptions.QilletniException;
import org.antlr.v4.runtime.ParserRuleContext;

public class UninjectableConstructorTypeException extends QilletniException {

    public UninjectableConstructorTypeException() {
        super();
    }

    public UninjectableConstructorTypeException(String message) {
        super(message);
    }

    public UninjectableConstructorTypeException(ParserRuleContext ctx) {
        super(ctx);
    }

    public UninjectableConstructorTypeException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public UninjectableConstructorTypeException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

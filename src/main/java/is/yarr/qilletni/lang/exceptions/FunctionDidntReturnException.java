package is.yarr.qilletni.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class FunctionDidntReturnException extends QilletniException {

    public FunctionDidntReturnException() {
    }

    public FunctionDidntReturnException(String message) {
        super(message);
    }

    public FunctionDidntReturnException(ParserRuleContext ctx) {
        super(ctx);
    }

    public FunctionDidntReturnException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public FunctionDidntReturnException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

package is.yarr.qilletni.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class VariableNotFoundException extends QilletniContextException {

    public VariableNotFoundException() {
    }

    public VariableNotFoundException(String message) {
        super(message);
    }

    public VariableNotFoundException(ParserRuleContext ctx) {
        super(ctx);
    }

    public VariableNotFoundException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public VariableNotFoundException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

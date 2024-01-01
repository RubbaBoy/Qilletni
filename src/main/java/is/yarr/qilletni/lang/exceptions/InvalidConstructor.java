package is.yarr.qilletni.lang.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class InvalidConstructor extends QilletniException {

    public InvalidConstructor() {
    }

    public InvalidConstructor(String message) {
        super(message);
    }

    public InvalidConstructor(ParserRuleContext ctx) {
        super(ctx);
    }

    public InvalidConstructor(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public InvalidConstructor(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

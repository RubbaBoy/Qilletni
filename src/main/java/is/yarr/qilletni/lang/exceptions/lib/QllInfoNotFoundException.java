package is.yarr.qilletni.lang.exceptions.lib;

import is.yarr.qilletni.api.exceptions.QilletniException;
import is.yarr.qilletni.lang.exceptions.QilletniContextException;
import org.antlr.v4.runtime.ParserRuleContext;

public class QllInfoNotFoundException extends QilletniContextException {
    public QllInfoNotFoundException() {
        super();
    }

    public QllInfoNotFoundException(String message) {
        super(message);
    }

    public QllInfoNotFoundException(ParserRuleContext ctx) {
        super(ctx);
    }

    public QllInfoNotFoundException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public QllInfoNotFoundException(Throwable cause) {
        super(cause);
    }

    public QllInfoNotFoundException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

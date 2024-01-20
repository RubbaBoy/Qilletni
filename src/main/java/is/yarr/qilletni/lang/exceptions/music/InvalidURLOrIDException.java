package is.yarr.qilletni.lang.exceptions.music;

import is.yarr.qilletni.lang.exceptions.QilletniException;
import org.antlr.v4.runtime.ParserRuleContext;

public class InvalidURLOrIDException extends QilletniException {

    public InvalidURLOrIDException() {
    }

    public InvalidURLOrIDException(String message) {
        super(message);
    }

    public InvalidURLOrIDException(ParserRuleContext ctx) {
        super(ctx);
    }

    public InvalidURLOrIDException(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public InvalidURLOrIDException(ParserRuleContext ctx, Throwable cause) {
        super(ctx, cause);
    }
}

package is.yarr.qilletni.lang.exceptions;

import is.yarr.qilletni.api.exceptions.QilletniException;
import is.yarr.qilletni.api.lang.stack.QilletniStackTrace;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;

public class QilletniContextException extends QilletniException {
    
    private String message;
    private String sourceLocation;
    private QilletniStackTrace stackTrace;
    
    public QilletniContextException() {
        this("");
    }

    public QilletniContextException(String message) {
        this.message = message;
    }

    public QilletniContextException(ParserRuleContext ctx) {
        this.sourceLocation = createMessage(ctx);
    }

    public QilletniContextException(ParserRuleContext ctx, String message) {
        this.message = message;
        setSource(ctx);
    }
    
    public QilletniContextException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
        setStackTrace(cause.getStackTrace());
    }
    
    public QilletniContextException(ParserRuleContext ctx, Throwable cause) {
        super(cause);
        setStackTrace(cause.getStackTrace());
        this.message = cause.getMessage();
        setSource(ctx);
    }
    
    public void setSource(ParserRuleContext ctx) {
        this.sourceLocation = createMessage(ctx);
    }

    @Override
    public String getMessage() {
        return String.format("%s\n%s\n\n%s\n\nInternal stack trace:", Objects.requireNonNullElse(message, ""), sourceLocation, stackTrace != null ? stackTrace.displayStackTrace() : "");
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSourceSet() {
        return sourceLocation != null;
    }

    public QilletniStackTrace getQilletniStackTrace() {
        return stackTrace;
    }

    public void setQilletniStackTrace(QilletniStackTrace stackTrace) {
        if (this.stackTrace != null) {
            return;
        }

        this.stackTrace = stackTrace;
    }

    private static String createMessage(ParserRuleContext ctx) {
        var startToken = ctx.getStart();
        int lineNum = startToken.getLine();
        int charPositionInLine = startToken.getCharPositionInLine();

        // Retrieve the input stream and extract the line
        var inputStream = startToken.getInputStream();
        var input = inputStream.toString();
        var lines = input.split("\n");
        var errorLine = lines[lineNum - 1]; // -1 because line numbers start at 1

        // Print the line with the error

        return String.format("In %s at %d:%d\n%s\n%s^", inputStream.getSourceName(), lineNum, charPositionInLine,
                errorLine,
                " ".repeat(Math.max(0, charPositionInLine)));
    }
    
}

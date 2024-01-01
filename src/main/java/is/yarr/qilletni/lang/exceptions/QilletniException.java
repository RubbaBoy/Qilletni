package is.yarr.qilletni.lang.exceptions;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.ParserRuleContext;

public class QilletniException extends RuntimeException {
    
    private String message;
    private boolean sourceSet;
    
    public QilletniException() {
        this("");
    }

    public QilletniException(String message) {
        this.message = message;
    }

    public QilletniException(ParserRuleContext ctx) {
        this.message = "\n" + createMessage(ctx);
        this.sourceSet = true;
    }

    public QilletniException(ParserRuleContext ctx, String message) {
        this.message = String.format("%s\n%s", message, createMessage(ctx));
        this.sourceSet = true;
    }
    
    public QilletniException(ParserRuleContext ctx, Throwable cause) {
        super(cause);
        
        this.message = "\n" + createMessage(ctx);
        this.sourceSet = true;
    }
    
    public void setSource(ParserRuleContext ctx) {
        message = String.format("%s\n%s", message, createMessage(ctx));
        sourceSet = true;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSourceSet() {
        return sourceSet;
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

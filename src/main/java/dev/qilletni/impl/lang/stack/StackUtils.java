package dev.qilletni.impl.lang.stack;

import dev.qilletni.impl.antlr.QilletniParser;
import org.antlr.v4.runtime.ParserRuleContext;

public class StackUtils {
    
    public static ParsedContext parseContext(QilletniParser.Function_callContext ctx) {
        var startToken = ctx.getStart();
        int lineNum = startToken.getLine();
        int charPositionInLine = startToken.getCharPositionInLine();
        var inputStream = startToken.getInputStream();
        
        return new ParsedContext(inputStream.getSourceName(), ctx.ID().getText(), lineNum, charPositionInLine);
    }
    
    public record ParsedContext(String fileName, String methodName, int line, int column) {}
    
}

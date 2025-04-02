package dev.qilletni.impl.lang.exceptions;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class REPLErrorListener extends BaseErrorListener {
    
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        // Get the input stream and current context
        String input = "";
        
        var inputStream = recognizer.getInputStream();
        
        if (inputStream instanceof CommonTokenStream commonTokenStream) {
            input = commonTokenStream.getTokenSource().getInputStream().toString();
        } else if (inputStream instanceof CodePointCharStream codePointCharStream) {
            input = codePointCharStream.toString();
        } else {
            System.err.printf("Unknown input stream type: %s%n", inputStream.getClass());
        }
        
        // Print the error message
        System.err.printf("Error at column %d: %s%n", charPositionInLine, msg);

//        System.err.println("input = " + input);

        // Print context information
        String[] lines = input.split("\n");
        if (line - 1 < lines.length) {
            String errorLine = lines[line - 1];
            System.err.printf("Context: %s%n", errorLine);
            System.err.printf("%s^%n", " ".repeat(charPositionInLine + 9));
        }
    }
}
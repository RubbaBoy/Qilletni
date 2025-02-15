package is.yarr.qilletni.lang.exceptions;

import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.api.exceptions.QilletniException;
import is.yarr.qilletni.api.lang.stack.QilletniStackTrace;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
        setMessage(message);
        setSource(ctx);
    }
    
    public QilletniContextException(Throwable cause) {
//        super(cause);
        setMessage(cause.getMessage());
        setStackTrace(cause.getStackTrace());
    }
    
    public QilletniContextException(ParserRuleContext ctx, Throwable cause) {
        this(ctx);
        setMessage(cause.getMessage());
        setStackTrace(cause.getStackTrace());
        
        // Optionally, copy suppressed exceptions if desired.
        for (Throwable suppressed : cause.getSuppressed()) {
            addSuppressed(suppressed);
        }
//        super(cause);
//        setStackTrace(cause.getStackTrace());
//        this.message = cause.getMessage();
//        setSource(ctx);
    }


    public void setSource(ParserRuleContext ctx) {
        this.sourceLocation = createMessage(ctx);
    }

    @Override
    public String getMessage() {
//        return String.format("<%s-start msg: %s %s-end>\n<srcloc: %s>\n\n%s\n\nInternal stack trace:", getClass().getCanonicalName(), Objects.requireNonNullElse(message, ""), getClass().getCanonicalName(), sourceLocation, stackTrace != null ? stackTrace.displayStackTrace() : "");
        return String.format("%s\n%s\n\n%s\n\nLibrary stack trace:\n%s\n\nInternal stack trace:", Objects.requireNonNullElse(message, ""), sourceLocation, stackTrace != null ? stackTrace.displayStackTrace() : "", getLibraryStackTrace());
    }
    
    private class StackTracePlaceholder {
        private StackTraceElement element;
        private int hidden;
        
        public StackTracePlaceholder(StackTraceElement element) {
            this.element = element;
            this.hidden = -1;
        }
        
        public StackTracePlaceholder() {
            this.hidden = 1;
            this.element = null;
        }
        
        public void incrementHidden() {
            hidden++;
        }
        
        public boolean isHidden() {
            return hidden != -1;
        }

        @Override
        public String toString() {
            if (isHidden()) {
                return "\t\u001B[37m<%d internal calls>\u001B[0m".formatted(hidden);
            } else {
                return "\tat %s".formatted(element);
            }
        }
    }
    
    // TODO: Make this use the module system to only show library stack trace
    private String getLibraryStackTrace() {
        StackTraceElement[] stackTraceElements = getStackTrace();
//        StringBuilder filteredStackTrace = new StringBuilder();
        var placeholders = new ArrayList<StackTracePlaceholder>();
        var ignorePackages = Set.of(
                "is.yarr.qilletni.lang",
                "is.yarr.qilletni.music",
                "is.yarr.qilletni.antlr",
                "is.yarr.qilletni.toolchain",
                "java.base",
                "org.antlr",
                "picocli"
        );

        System.out.println("modules:");
        for (StackTraceElement element : stackTraceElements) {
            System.out.println(element.getModuleName() + "  - " + element.getClassName());
            if (ignorePackages.stream().noneMatch(pkg -> element.getClassName().startsWith(pkg))) {
                placeholders.add(new StackTracePlaceholder(element));
//                filteredStackTrace.append("\tat ").append(element).append("\n");
            } else { // Hidden package
                if (placeholders.isEmpty() || !placeholders.getLast().isHidden()) {
                    placeholders.add(new StackTracePlaceholder());
                } else {
                    placeholders.getLast().incrementHidden();
                }
            }
        }
    
        return placeholders.stream().map(Objects::toString).collect(Collectors.joining("\n"));
    }
    
    public String getOriginalMessage() {
        return message;
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
        var startToken = getStartToken(ctx);
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
    
    private static Token getStartToken(ParserRuleContext ctx) {
        if (ctx instanceof QilletniParser.Import_fileContext importFileContext) {
            // Can't find file
            return importFileContext.STRING().getSymbol();
        }
        
        return ctx.getStart();
    }
    
}

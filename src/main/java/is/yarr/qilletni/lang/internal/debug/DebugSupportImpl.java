package is.yarr.qilletni.lang.internal.debug;

import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.api.lang.internal.debug.DebugSupport;
import is.yarr.qilletni.api.lang.stack.QilletniStackTrace;
import is.yarr.qilletni.api.lang.table.SymbolTable;
import is.yarr.qilletni.lang.QilletniVisitor;
import is.yarr.qilletni.lang.exceptions.REPLErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class DebugSupportImpl implements DebugSupport {
    
    private final boolean isDebugEnabled;
    private String lastExecutedLine = "<none>";
    private boolean insideBreakpoint = false;
    
    // Specific to the invocation
    private SymbolTable symbolTable;
    private QilletniVisitor qilletniVisitor;
    private QilletniStackTrace qilletniStackTrace;
    private int debugLineCount = 0; // The number of lines invoked via runDebugLine()

    public DebugSupportImpl(boolean isDebugEnabled) {
        this.isDebugEnabled = isDebugEnabled;
    }

    public void setLastExecutedLine(String lastExecutedLine) {
        this.lastExecutedLine = lastExecutedLine;
    }
    
    public void initializeREPL(SymbolTable symbolTable, QilletniVisitor qilletniVisitor, QilletniStackTrace qilletniStackTrace) {
        this.symbolTable = symbolTable;
        this.qilletniVisitor = qilletniVisitor;
        this.qilletniStackTrace = qilletniStackTrace;
        this.debugLineCount = 0;
    }

    @Override
    public String getLastExecutedFunctionLine() {
        return lastExecutedLine;
    }

    @Override
    public int runDebugLine(String line) {
        var lexer = new QilletniLexer(CharStreams.fromString(line, "REPL#%d".formatted(++debugLineCount)));
        var tokenStream = new CommonTokenStream(lexer);
        var qilletniParser = new QilletniParser(tokenStream);

        lexer.addErrorListener(new REPLErrorListener());
        qilletniParser.addErrorListener(new REPLErrorListener());
        
        QilletniParser.ProgContext programContext = qilletniParser.prog();

        qilletniVisitor.visit(programContext);
        
        return 0;
    }

    @Override
    public boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    @Override
    public void printBacktrace() {
        qilletniStackTrace.displayStackTrace();
    }

    @Override
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    @Override
    public void enterBreakpoint() {
        insideBreakpoint = true;
    }

    @Override
    public void exitBreakpoint() {
        insideBreakpoint = false;
    }

    @Override
    public boolean isInBreakpoint() {
        return insideBreakpoint;
    }

}

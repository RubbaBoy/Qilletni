package is.yarr.qilletni;

import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.table.Scope;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) throws IOException {
        LOGGER.info("Hello world!");
        
        new Main().main(args[0]);
    }
    
    private void main(String programFile) throws IOException {
        var input = CharStreams.fromPath(Paths.get("input", programFile));
        
        var lexer = new QilletniLexer(input);
        var tokenStream = new CommonTokenStream(lexer);
        var qilletniParser = new QilletniParser(tokenStream);
        
        var symbolTable = new SymbolTable();
        var nativeFunctionHandler = new NativeFunctionHandler();
        nativeFunctionHandler.init(InternalNative.class);
        
        QilletniParser.ProgContext programContext = qilletniParser.prog();
        var qilletniVisitor = new QilletniVisitor(symbolTable, nativeFunctionHandler);
        qilletniVisitor.visit(programContext);

        LOGGER.debug("Symbol table at the end:");

        qilletniVisitor.symbolTable.getAllScopes()
                .stream()
                .map(Scope::toString)
                .forEach(LOGGER::debug);
    }
}
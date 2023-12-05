package is.yarr.qilletni.lang;

import is.yarr.qilletni.InternalNative;
import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.lang.internal.StringNativeFunctions;
import is.yarr.qilletni.lang.table.SymbolTable;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class QilletniProgramRunner {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(QilletniProgramRunner.class);
    
    private final SymbolTable symbolTable;

    public QilletniProgramRunner() {
        this.symbolTable = new SymbolTable();
    }

    public QilletniProgramRunner(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
    
    public void runProgram(Path file) throws IOException {
        var input = CharStreams.fromPath(file);

        var lexer = new QilletniLexer(input);
        var tokenStream = new CommonTokenStream(lexer);
        var qilletniParser = new QilletniParser(tokenStream);

        var nativeFunctionHandler = new NativeFunctionHandler();
        nativeFunctionHandler.init(InternalNative.class, StringNativeFunctions.class);

        QilletniParser.ProgContext programContext = qilletniParser.prog();
        var qilletniVisitor = new QilletniVisitor(symbolTable, nativeFunctionHandler, importedFile -> importFile(importedFile, file));
        qilletniVisitor.visit(programContext);
    }
    
    private void importFile(String filePath, Path parentFile) {
        var importPath = parentFile.getParent().resolve(filePath.substring(1, filePath.length() - 1));
        LOGGER.debug("Importing file {}", importPath);

        try {
            runProgram(importPath);
        } catch (IOException e) {
            LOGGER.error("Unable to import file " + importPath, e);
        }
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}

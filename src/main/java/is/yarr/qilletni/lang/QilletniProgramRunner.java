package is.yarr.qilletni.lang;

import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.lib.LibraryInit;
import is.yarr.qilletni.lang.internal.NativeFunctionHandler;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapterInvoker;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapterRegistrar;
import is.yarr.qilletni.lang.table.Scope;
import is.yarr.qilletni.lang.table.SymbolTable;
import is.yarr.qilletni.lang.types.BooleanType;
import is.yarr.qilletni.lang.types.IntType;
import is.yarr.qilletni.lang.types.StringType;
import is.yarr.qilletni.lang.types.entity.EntityDefinitionManager;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class QilletniProgramRunner {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(QilletniProgramRunner.class);
    
    private final SymbolTable symbolTable;
    private final Scope globalScope;
    private final EntityDefinitionManager entityDefinitionManager;

    public QilletniProgramRunner() {
        this.symbolTable = new SymbolTable();
        this.globalScope = new Scope();
        this.entityDefinitionManager = new EntityDefinitionManager();
    }

    public QilletniProgramRunner(SymbolTable symbolTable, Scope globalScope, EntityDefinitionManager entityDefinitionManager) {
        this.symbolTable = symbolTable;
        this.globalScope = globalScope;
        this.entityDefinitionManager = entityDefinitionManager;
    }
    
    public void runProgram(Path file) throws IOException {
        var input = CharStreams.fromPath(file);

        var lexer = new QilletniLexer(input);
        var tokenStream = new CommonTokenStream(lexer);
        var qilletniParser = new QilletniParser(tokenStream);

        var typeAdapterRegistrar = new TypeAdapterRegistrar();
        var typeAdapterInvoker = new TypeAdapterInvoker(typeAdapterRegistrar);
        var nativeFunctionHandler = new NativeFunctionHandler(typeAdapterInvoker);

        LibraryInit.registerFunctions(nativeFunctionHandler);
        
        typeAdapterRegistrar.registerTypeAdapter(BooleanType.class, Boolean.class, BooleanType::new);
        typeAdapterRegistrar.registerTypeAdapter(boolean.class, BooleanType.class, BooleanType::getValue);
        
        typeAdapterRegistrar.registerTypeAdapter(IntType.class, Integer.class, IntType::new);
        typeAdapterRegistrar.registerTypeAdapter(int.class, IntType.class, IntType::getValue);
        
        typeAdapterRegistrar.registerTypeAdapter(StringType.class, String.class, StringType::new);
        typeAdapterRegistrar.registerTypeAdapter(String.class, StringType.class, StringType::getValue);

        QilletniParser.ProgContext programContext = qilletniParser.prog();
        var qilletniVisitor = new QilletniVisitor(symbolTable, globalScope, entityDefinitionManager, nativeFunctionHandler, importedFile -> importFile(importedFile, file));
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

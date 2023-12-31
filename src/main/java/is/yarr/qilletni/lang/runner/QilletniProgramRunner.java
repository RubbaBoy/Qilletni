package is.yarr.qilletni.lang.runner;

import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.lang.QilletniVisitor;
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
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class QilletniProgramRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(QilletniProgramRunner.class);

    private final SymbolTable symbolTable;
    private final Scope globalScope;
    private final EntityDefinitionManager entityDefinitionManager;
    private final NativeFunctionHandler nativeFunctionHandler;

    public QilletniProgramRunner() {
        this.symbolTable = new SymbolTable();
        this.globalScope = new Scope();
        this.entityDefinitionManager = new EntityDefinitionManager();
        this.nativeFunctionHandler = createNativeFunctionHandler();
    }

    public QilletniProgramRunner(SymbolTable symbolTable, Scope globalScope, EntityDefinitionManager entityDefinitionManager, NativeFunctionHandler nativeFunctionHandler) {
        this.symbolTable = symbolTable;
        this.globalScope = globalScope;
        this.entityDefinitionManager = entityDefinitionManager;
        this.nativeFunctionHandler = nativeFunctionHandler;
    }

    private static NativeFunctionHandler createNativeFunctionHandler() {
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

        return nativeFunctionHandler;
    }

    public void runProgram(Path file) throws IOException {
        runProgram(file, new ImportPathState(file.getParent(), false));
    }

    private void runProgram(Path file, ImportPathState pathState) throws IOException {
        runProgram(CharStreams.fromPath(file), pathState);
    }

    private void runProgram(InputStream stream, ImportPathState pathState) throws IOException {
        runProgram(CharStreams.fromStream(stream), pathState);
    }

    public void runProgram(CharStream charStream, ImportPathState pathState) {
        var lexer = new QilletniLexer(charStream);
        var tokenStream = new CommonTokenStream(lexer);
        var qilletniParser = new QilletniParser(tokenStream);

        QilletniParser.ProgContext programContext = qilletniParser.prog();
        var qilletniVisitor = new QilletniVisitor(symbolTable, globalScope, entityDefinitionManager, nativeFunctionHandler, importedFile -> importFileFromStream(pathState.importFrom(importedFile)));
        qilletniVisitor.visit(programContext);
    }

    private void importFileFromStream(ImportPathState pathState) {
        try {
            if (pathState.isInternal()) {
                LOGGER.debug("Importing internal file: {}", pathState.path());
                
                runProgram(getClass().getClassLoader().getResourceAsStream(pathState.path().toString()), pathState);
            } else {
                LOGGER.debug("Importing filesystem file: {}", pathState.path());

                runProgram(pathState.path());
            }
        } catch (IOException e) {
            LOGGER.error("Unable to import file " + pathState, e);
        }
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public NativeFunctionHandler getNativeFunctionHandler() {
        return nativeFunctionHandler;
    }

    public Scope getGlobalScope() {
        return globalScope;
    }

    public EntityDefinitionManager getEntityDefinitionManager() {
        return entityDefinitionManager;
    }
}

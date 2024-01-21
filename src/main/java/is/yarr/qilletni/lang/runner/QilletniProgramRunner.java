package is.yarr.qilletni.lang.runner;

import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.lang.QilletniVisitor;
import is.yarr.qilletni.lang.types.list.ListTypeTransformer;
import is.yarr.qilletni.lang.types.list.ListTypeTransformerFactory;
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
import is.yarr.qilletni.api.music.MusicCache;
import is.yarr.qilletni.music.MusicPopulator;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class QilletniProgramRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(QilletniProgramRunner.class);

    private final List<SymbolTable> symbolTables;
    private final Scope globalScope;
    private final EntityDefinitionManager entityDefinitionManager;
    private final NativeFunctionHandler nativeFunctionHandler;
    private final MusicCache musicCache;
    private final MusicPopulator musicPopulator;
    private final ListTypeTransformer listTypeTransformer;

    public QilletniProgramRunner(MusicCache musicCache) {
        this.musicCache = musicCache;
        this.symbolTables = new ArrayList<>();
        this.globalScope = new Scope();
        this.entityDefinitionManager = new EntityDefinitionManager();
        this.nativeFunctionHandler = createNativeFunctionHandler();
        this.musicPopulator = new MusicPopulator(musicCache);
        
        var listGeneratorFactory = new ListTypeTransformerFactory();
        this.listTypeTransformer = listGeneratorFactory.createListGenerator();
    }

    public QilletniProgramRunner(Scope globalScope, EntityDefinitionManager entityDefinitionManager, NativeFunctionHandler nativeFunctionHandler, MusicCache musicCache, MusicPopulator musicPopulator, ListTypeTransformer listTypeTransformer) {
        this.musicCache = musicCache;
        this.musicPopulator = musicPopulator;
        this.listTypeTransformer = listTypeTransformer;
        this.symbolTables = new ArrayList<>();
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

    public SymbolTable runProgram(Path file) throws IOException {
        return runProgram(file, new ImportPathState(file.getParent(), false));
    }

    private SymbolTable runProgram(Path file, ImportPathState pathState) throws IOException {
        return runProgram(CharStreams.fromString(Files.readString(file), file.getFileName().toString()), pathState);
    }

    private SymbolTable runProgram(InputStream stream, ImportPathState pathState) throws IOException {
        var data = new String(new BufferedInputStream(stream).readAllBytes());
        return runProgram(CharStreams.fromString(data, pathState.path().getFileName().toString()), pathState);
    }

    public SymbolTable runProgram(CharStream charStream, ImportPathState pathState) {
        var lexer = new QilletniLexer(charStream);
        var tokenStream = new CommonTokenStream(lexer);
        var qilletniParser = new QilletniParser(tokenStream);
        
        var symbolTable = new SymbolTable();
        symbolTables.add(symbolTable);

        QilletniParser.ProgContext programContext = qilletniParser.prog();
        var qilletniVisitor = new QilletniVisitor(symbolTable, globalScope, entityDefinitionManager, nativeFunctionHandler, musicPopulator, listTypeTransformer, importedFile -> importFileFromStream(pathState.importFrom(importedFile)));
        qilletniVisitor.visit(programContext);
        
        return symbolTable;
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

    public List<SymbolTable> getSymbolTables() {
        return symbolTables;
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

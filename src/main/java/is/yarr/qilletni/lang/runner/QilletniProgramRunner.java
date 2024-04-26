package is.yarr.qilletni.lang.runner;

import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.api.exceptions.QilletniException;
import is.yarr.qilletni.api.lang.stack.QilletniStackTrace;
import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.api.lang.table.SymbolTable;
import is.yarr.qilletni.api.lang.types.BooleanType;
import is.yarr.qilletni.api.lang.types.ImportAliasType;
import is.yarr.qilletni.api.lang.types.IntType;
import is.yarr.qilletni.api.lang.types.JavaType;
import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.StringType;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.api.lang.types.entity.EntityInitializer;
import is.yarr.qilletni.api.music.MusicPopulator;
import is.yarr.qilletni.api.music.supplier.DynamicProvider;
import is.yarr.qilletni.lang.QilletniVisitor;
import is.yarr.qilletni.lang.exceptions.QilletniContextException;
import is.yarr.qilletni.lang.exceptions.QilletniNativeInvocationException;
import is.yarr.qilletni.lang.exceptions.TypeMismatchException;
import is.yarr.qilletni.lang.internal.NativeFunctionHandler;
import is.yarr.qilletni.lang.internal.UnimplementedFunctionInvoker;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapterInvoker;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapterRegistrar;
import is.yarr.qilletni.lang.stack.QilletniStackTraceImpl;
import is.yarr.qilletni.lang.table.ScopeImpl;
import is.yarr.qilletni.lang.table.SymbolTableImpl;
import is.yarr.qilletni.lang.types.BooleanTypeImpl;
import is.yarr.qilletni.lang.types.ImportAliasTypeImpl;
import is.yarr.qilletni.lang.types.IntTypeImpl;
import is.yarr.qilletni.lang.types.JavaTypeImpl;
import is.yarr.qilletni.lang.types.ListTypeImpl;
import is.yarr.qilletni.lang.types.StringTypeImpl;
import is.yarr.qilletni.lang.types.entity.EntityDefinitionManagerImpl;
import is.yarr.qilletni.lang.types.entity.EntityInitializerImpl;
import is.yarr.qilletni.lang.types.list.ListTypeTransformer;
import is.yarr.qilletni.lang.types.list.ListTypeTransformerFactory;
import is.yarr.qilletni.lib.LibraryRegistrar;
import is.yarr.qilletni.lib.LibrarySourceFileResolver;
import is.yarr.qilletni.music.MusicPopulatorImpl;
import is.yarr.qilletni.music.factories.AlbumTypeFactoryImpl;
import is.yarr.qilletni.music.factories.CollectionTypeFactoryImpl;
import is.yarr.qilletni.music.factories.SongTypeFactoryImpl;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class QilletniProgramRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(QilletniProgramRunner.class);

    private final Map<SymbolTable, QilletniVisitor> symbolTables;
    private final ScopeImpl globalScope;
    private final EntityDefinitionManager entityDefinitionManager;
    private final TypeAdapterRegistrar typeAdapterRegistrar;
    private final NativeFunctionHandler nativeFunctionHandler;
    private final EntityInitializer entityInitializer;
    private final DynamicProvider dynamicProvider;
    private final MusicPopulator musicPopulator;
    private final ListTypeTransformer listTypeTransformer;
    private final LibraryRegistrar libraryRegistrar;
    private final QilletniStackTrace qilletniStackTrace;

    public QilletniProgramRunner(DynamicProvider dynamicProvider, LibrarySourceFileResolver librarySourceFileResolver, ClassLoader libraryClassLoader) {
        this.dynamicProvider = dynamicProvider;
        this.symbolTables = new HashMap<>();
        this.globalScope = new ScopeImpl("global");
        this.entityDefinitionManager = new EntityDefinitionManagerImpl();
        this.typeAdapterRegistrar = createTypeAdapterRegistrar();
        this.nativeFunctionHandler = createNativeFunctionHandler(typeAdapterRegistrar, symbolTables);
        this.entityInitializer = new EntityInitializerImpl(typeAdapterRegistrar, entityDefinitionManager);
        this.musicPopulator = new MusicPopulatorImpl(dynamicProvider);
        this.qilletniStackTrace = new QilletniStackTraceImpl();


        var songTypeFactory = new SongTypeFactoryImpl();
        var collectionTypeFactory = new CollectionTypeFactoryImpl();
        var albumTypeFactory = new AlbumTypeFactoryImpl();

        dynamicProvider.initFactories(songTypeFactory, collectionTypeFactory, albumTypeFactory);

        var listGeneratorFactory = new ListTypeTransformerFactory();
        this.listTypeTransformer = listGeneratorFactory.createListGenerator();
        this.libraryRegistrar = new LibraryRegistrar(nativeFunctionHandler, librarySourceFileResolver);

        libraryRegistrar.registerLibraries(libraryClassLoader);

        nativeFunctionHandler.addInjectableInstance(musicPopulator);
        nativeFunctionHandler.addInjectableInstance(entityDefinitionManager);
        nativeFunctionHandler.addInjectableInstance(entityInitializer);
        nativeFunctionHandler.addInjectableInstance(songTypeFactory);
        nativeFunctionHandler.addInjectableInstance(collectionTypeFactory);
        nativeFunctionHandler.addInjectableInstance(albumTypeFactory);
        nativeFunctionHandler.addInjectableInstance(new UnimplementedFunctionInvoker());
    }

    private static TypeAdapterRegistrar createTypeAdapterRegistrar() {
        var typeAdapterRegistrar = new TypeAdapterRegistrar();

        typeAdapterRegistrar.registerExactTypeAdapter(BooleanTypeImpl.class, Boolean.class, BooleanTypeImpl::new);
        typeAdapterRegistrar.registerExactTypeAdapter(boolean.class, BooleanTypeImpl.class, BooleanType::getValue);

        typeAdapterRegistrar.registerExactTypeAdapter(IntTypeImpl.class, Long.class, IntTypeImpl::new);
        typeAdapterRegistrar.registerExactTypeAdapter(long.class, IntTypeImpl.class, IntType::getValue);

        typeAdapterRegistrar.registerExactTypeAdapter(IntTypeImpl.class, Integer.class, i -> new IntTypeImpl((long) i));
        typeAdapterRegistrar.registerExactTypeAdapter(int.class, IntTypeImpl.class, intType -> (int) intType.getValue());

        typeAdapterRegistrar.registerExactTypeAdapter(StringTypeImpl.class, String.class, StringTypeImpl::new);
        typeAdapterRegistrar.registerExactTypeAdapter(String.class, StringTypeImpl.class, StringType::getValue);

        typeAdapterRegistrar.registerTypeAdapter(ListType.class, List.class, list -> {
            var qilletniList = (List<QilletniType>) list;

            var typeList = qilletniList.stream().map(QilletniType::getTypeClass).distinct().toList();
            if (typeList.size() > 1) {
                throw new TypeMismatchException("Multiple types found in list");
            }

            return new ListTypeImpl(qilletniList.get(0).getTypeClass(), qilletniList);
        });

        typeAdapterRegistrar.registerTypeAdapter(JavaType.class, Object.class, JavaTypeImpl::new);

        return typeAdapterRegistrar;
    }

    private static NativeFunctionHandler createNativeFunctionHandler(TypeAdapterRegistrar typeAdapterRegistrar, Map<SymbolTable, QilletniVisitor> symbolTables) {
        return new NativeFunctionHandler(new TypeAdapterInvoker(typeAdapterRegistrar), symbolTables);
    }

    public void importInitialFiles() {
        LOGGER.debug("Importing {} initial files!", libraryRegistrar.getAutoImportFiles().size());

        libraryRegistrar.getAutoImportFiles().forEach(autoImportFile ->
                importFileFromStream(new ImportPathState(Paths.get(autoImportFile.fileName()), autoImportFile.libName())));
    }

    public SymbolTable runProgram(Path file) throws IOException {
        return runProgram(file, null);
    }

    public SymbolTable runProgram(Path file, Scope global) throws IOException {
        return runProgram(file, new ImportPathState(file.getParent()), global);
    }

    private SymbolTable runProgram(Path file, ImportPathState pathState, Scope global) throws IOException {
        return runProgram(CharStreams.fromString(Files.readString(file), file.getFileName().toString()), pathState, global);
    }

    private SymbolTable runProgram(InputStream stream, ImportPathState pathState, Scope globalScope) throws IOException {
        var data = new String(new BufferedInputStream(stream).readAllBytes());
        return runProgram(CharStreams.fromString(data, pathState.path().getFileName().toString()), pathState, globalScope);
    }

    private SymbolTable runProgram(String data, ImportPathState pathState, Scope globalScope) throws IOException {
//        var data = new String(new BufferedInputStream(stream).readAllBytes());
        return runProgram(CharStreams.fromString(data, pathState.path().getFileName().toString()), pathState, globalScope);
    }

    public SymbolTable runProgram(CharStream charStream, ImportPathState pathState) {
        return runProgram(charStream, pathState, null);
    }

    public SymbolTable runProgram(CharStream charStream, ImportPathState pathState, Scope globalScope) {
        var lexer = new QilletniLexer(charStream);
        var tokenStream = new CommonTokenStream(lexer);
        var qilletniParser = new QilletniParser(tokenStream);

        var globalOverride = globalScope == null ? this.globalScope : globalScope;

        var symbolTable = new SymbolTableImpl(pathState.path().toString());

        LOGGER.debug("global override: {}", globalScope);

        QilletniParser.ProgContext programContext = qilletniParser.prog();
        var qilletniVisitor = new QilletniVisitor(symbolTable, symbolTables, globalOverride, dynamicProvider, entityDefinitionManager, nativeFunctionHandler, musicPopulator, listTypeTransformer, qilletniStackTrace, (importedFile, importAs) -> importFileFromStream(pathState.importFrom(importedFile), importAs, globalOverride));
        symbolTables.put(symbolTable, qilletniVisitor);

        qilletniVisitor.visit(programContext);

        return symbolTable;
    }

    private Optional<ImportAliasType> importFileFromStream(ImportPathState pathState) {
        return importFileFromStream(pathState, null, globalScope);
    }

    private Optional<ImportAliasType> importFileFromStream(ImportPathState pathState, String importAs, Scope global) {
        try {
            if (importAs != null) {
                global = new ScopeImpl(globalScope, Scope.ScopeType.ALIASED_GLOBAL, "fake global");
            }

            var symbolTable = runProgram(libraryRegistrar.findLibraryByPath(pathState.libraryName(), pathState.path()).orElseThrow(FileNotFoundException::new), pathState, global);

            if (importAs != null) {
                LOGGER.debug("Creating import alias '{}' for: {}", importAs, symbolTable.currentScope());
                return Optional.of(new ImportAliasTypeImpl(importAs, symbolTable.currentScope()));
            }
        } catch (IOException e) {
            LOGGER.error("Unable to import file " + pathState, e);

        } catch (Exception e) {
            var qce = new QilletniNativeInvocationException(e);
            qce.setQilletniStackTrace(qilletniStackTrace);
            qce.setMessage(e.getMessage());
            throw qce;
        }

        return Optional.empty();
    }

    public Map<SymbolTable, QilletniVisitor> getSymbolTables() {
        return symbolTables;
    }

    public NativeFunctionHandler getNativeFunctionHandler() {
        return nativeFunctionHandler;
    }

    public LibraryRegistrar getLibraryRegistrar() {
        return libraryRegistrar;
    }

    public Scope getGlobalScope() {
        return globalScope;
    }

    public EntityDefinitionManager getEntityDefinitionManager() {
        return entityDefinitionManager;
    }
}

package is.yarr.qilletni.lang.runner;

import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.api.auth.ServiceProvider;
import is.yarr.qilletni.api.lang.types.JavaType;
import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.music.StringIdentifier;
import is.yarr.qilletni.api.music.TrackOrchestrator;
import is.yarr.qilletni.lang.QilletniVisitor;
import is.yarr.qilletni.lang.exceptions.TypeMismatchException;
import is.yarr.qilletni.lang.table.ScopeImpl;
import is.yarr.qilletni.lang.types.BooleanTypeImpl;
import is.yarr.qilletni.lang.types.IntTypeImpl;
import is.yarr.qilletni.lang.types.JavaTypeImpl;
import is.yarr.qilletni.lang.types.ListTypeImpl;
import is.yarr.qilletni.lang.types.StringTypeImpl;
import is.yarr.qilletni.lang.types.entity.EntityDefinitionManagerImpl;
import is.yarr.qilletni.lang.types.list.ListTypeTransformer;
import is.yarr.qilletni.lang.types.list.ListTypeTransformerFactory;
import is.yarr.qilletni.lang.internal.NativeFunctionHandler;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapterInvoker;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapterRegistrar;
import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.lang.table.SymbolTable;
import is.yarr.qilletni.api.lang.types.BooleanType;
import is.yarr.qilletni.api.lang.types.IntType;
import is.yarr.qilletni.api.lang.types.StringType;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.api.music.MusicCache;
import is.yarr.qilletni.lib.LibraryRegistrar;
import is.yarr.qilletni.api.music.MusicPopulator;
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
import java.util.ArrayList;
import java.util.List;

public class QilletniProgramRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(QilletniProgramRunner.class);

    private final List<SymbolTable> symbolTables;
    private final ScopeImpl globalScope;
    private final EntityDefinitionManager entityDefinitionManager;
    private final NativeFunctionHandler nativeFunctionHandler;
    private final MusicCache musicCache;
    private final MusicPopulator musicPopulator;
    private final ListTypeTransformer listTypeTransformer;
    private final LibraryRegistrar libraryRegistrar;
    private final TrackOrchestrator trackOrchestrator;
    private final StringIdentifier stringIdentifier;

    public QilletniProgramRunner(ServiceProvider serviceProvider) {
        this.musicCache = serviceProvider.getMusicCache();
        this.trackOrchestrator = serviceProvider.getTrackOrchestrator();
        this.symbolTables = new ArrayList<>();
        this.globalScope = new ScopeImpl();
        this.entityDefinitionManager = new EntityDefinitionManagerImpl();
        this.nativeFunctionHandler = createNativeFunctionHandler();
        this.musicPopulator = new MusicPopulatorImpl(musicCache);


        var songTypeFactory = new SongTypeFactoryImpl();
        var collectionTypeFactory = new CollectionTypeFactoryImpl();
        var albumTypeFactory = new AlbumTypeFactoryImpl();

        this.stringIdentifier = serviceProvider.getStringIdentifier(songTypeFactory, collectionTypeFactory, albumTypeFactory);
        
        var listGeneratorFactory = new ListTypeTransformerFactory();
        this.listTypeTransformer = listGeneratorFactory.createListGenerator();
        this.libraryRegistrar = new LibraryRegistrar(nativeFunctionHandler);
        
        libraryRegistrar.registerLibraries();

        nativeFunctionHandler.addInjectableInstance(musicPopulator);
        nativeFunctionHandler.addInjectableInstance(entityDefinitionManager);
        nativeFunctionHandler.addInjectableInstance(songTypeFactory);
        nativeFunctionHandler.addInjectableInstance(collectionTypeFactory);
        nativeFunctionHandler.addInjectableInstance(albumTypeFactory);
    }

    public QilletniProgramRunner(ScopeImpl globalScope, EntityDefinitionManager entityDefinitionManager, NativeFunctionHandler nativeFunctionHandler, MusicCache musicCache, MusicPopulator musicPopulator, ListTypeTransformer listTypeTransformer, LibraryRegistrar libraryRegistrar, TrackOrchestrator trackOrchestrator, StringIdentifier stringIdentifier) {
        this.musicCache = musicCache;
        this.musicPopulator = musicPopulator;
        this.listTypeTransformer = listTypeTransformer;
        this.trackOrchestrator = trackOrchestrator;
        this.stringIdentifier = stringIdentifier;
        this.symbolTables = new ArrayList<>();
        this.globalScope = globalScope;
        this.entityDefinitionManager = entityDefinitionManager;
        this.nativeFunctionHandler = nativeFunctionHandler;
        this.libraryRegistrar = libraryRegistrar;
    }

    private static NativeFunctionHandler createNativeFunctionHandler() {
        var typeAdapterRegistrar = new TypeAdapterRegistrar();
        var typeAdapterInvoker = new TypeAdapterInvoker(typeAdapterRegistrar);
        var nativeFunctionHandler = new NativeFunctionHandler(typeAdapterInvoker);

        typeAdapterRegistrar.registerExactTypeAdapter(BooleanTypeImpl.class, Boolean.class, BooleanTypeImpl::new);
        typeAdapterRegistrar.registerExactTypeAdapter(boolean.class, BooleanTypeImpl.class, BooleanType::getValue);

        typeAdapterRegistrar.registerExactTypeAdapter(IntTypeImpl.class, Integer.class, IntTypeImpl::new);
        typeAdapterRegistrar.registerExactTypeAdapter(int.class, IntTypeImpl.class, IntType::getValue);

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

        return nativeFunctionHandler;
    }
    
    public void importInitialFiles() {
        LOGGER.debug("Importing initial files!");
        
        libraryRegistrar.getAutoImportFiles().forEach(autoImportFile -> {
            importFileFromStream(new ImportPathState(Paths.get(autoImportFile.libName() + "\\" + autoImportFile.fileName()), true, autoImportFile.libName()));
        });
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
        var qilletniVisitor = new QilletniVisitor(symbolTable, globalScope, entityDefinitionManager, nativeFunctionHandler, musicPopulator, listTypeTransformer, trackOrchestrator, stringIdentifier, musicCache, importedFile -> importFileFromStream(pathState.importFrom(importedFile)));
        qilletniVisitor.visit(programContext);
        
        return symbolTable;
    }

    private void importFileFromStream(ImportPathState pathState) {
        try {
            if (pathState.isInternal()) {
                LOGGER.debug("Importing internal file: {}", pathState.path());
                
//                runProgram(getClass().getClassLoader().getResourceAsStream(pathState.path().toString()), pathState);
                runProgram(libraryRegistrar.findLibraryByPath(pathState.libraryName(), pathState.path()).orElseThrow(FileNotFoundException::new), pathState);
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

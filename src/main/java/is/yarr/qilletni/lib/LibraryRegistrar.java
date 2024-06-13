package is.yarr.qilletni.lib;

import is.yarr.qilletni.api.lib.Library;
import is.yarr.qilletni.lang.exceptions.lib.LibraryNotFoundException;
import is.yarr.qilletni.lang.internal.NativeFunctionHandler;
import is.yarr.qilletni.lang.runner.ImportPathState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public class LibraryRegistrar {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryRegistrar.class);
    
    private final NativeFunctionHandler nativeFunctionHandler;
    private final LibrarySourceFileResolver librarySourceFileResolver; 
    private final Map<String, Library> libraries = new HashMap<>();
    private final List<AutoImportFile> autoImportFiles = new ArrayList<>();

    public LibraryRegistrar(NativeFunctionHandler nativeFunctionHandler, LibrarySourceFileResolver librarySourceFileResolver) {
        this.nativeFunctionHandler = nativeFunctionHandler;
        this.librarySourceFileResolver = librarySourceFileResolver;
    }

    public void registerLibraries(ClassLoader libraryClassLoader) {
        for (var library : ServiceLoader.load(Library.class, libraryClassLoader)) {
            var qllInfo = QilletniInfoReader.getQllInfo(library);
            LOGGER.debug("Loading library {} v{}", qllInfo.name(), qllInfo.version().getVersionString());

            if (libraries.containsKey(qllInfo.name())) {
                LOGGER.error("Attempted to import library of duplicate name: {}", qllInfo.name());
            } else {
                libraries.put(qllInfo.name(), library);
                nativeFunctionHandler.registerClasses(library.getNativeClasses().toArray(Class<?>[]::new));
                library.supplyNativeFunctionBindings(nativeFunctionHandler);

                autoImportFiles.addAll(library.autoImportFiles()
                        .stream()
                        .map(file -> new AutoImportFile(file, qllInfo.name())).toList());
            }
        }
    }

    /**
     * Reads a .ql file from a library, returning its contents.
     * 
     * @param libraryName The name of the library
     * @param path The path of the file
     * @return The contents of the file, if present
     */
    public Optional<String> findLibraryByPath(String libraryName, Path path) throws IOException {
        if (libraryName == null) {
            try (var resourceStream = getClass().getClassLoader().getResourceAsStream(path.toString())) {
                if (resourceStream != null) {
                    return Optional.of(new String(resourceStream.readAllBytes()));
                }
                
                return Optional.empty();
            }
        }

        if (!libraries.containsKey(libraryName)) {
            throw new LibraryNotFoundException("Library '%s' not found".formatted(libraryName));
        }
        
        var library = libraries.get(libraryName);
        
        return librarySourceFileResolver.resolveSourcePath(libraryName, path.toString().replace("\\", "/"))
                .or(() -> library.readPath(path.toString().replace("\\", "/")).map(stream -> {
                    try {
                        return new String(stream.readAllBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    public LibrarySourceFileResolver getLibrarySourceFileResolver() {
        return librarySourceFileResolver;
    }

    public List<AutoImportFile> getAutoImportFiles() {
        return autoImportFiles;
    }

    public record AutoImportFile(String fileName, String libName) {}
}

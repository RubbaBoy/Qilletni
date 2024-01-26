package is.yarr.qilletni.lib;

import is.yarr.qilletni.api.lib.Library;
import is.yarr.qilletni.lang.internal.NativeFunctionHandler;
import is.yarr.qilletni.lang.runner.ImportPathState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
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
    private final Map<String, Library> libraries = new HashMap<>();
    private final List<AutoImportFile> autoImportFiles = new ArrayList<>();

    public LibraryRegistrar(NativeFunctionHandler nativeFunctionHandler) {
        this.nativeFunctionHandler = nativeFunctionHandler;
    }

    public void registerLibraries() {
        for (var library : ServiceLoader.load(Library.class)) {
            LOGGER.debug("Loading library {} v{}", library.getName(), library.getVersion());

            if (libraries.containsKey(library.getImportName())) {
                LOGGER.error("Attempted to import library {} with duplicate import name of {}", library.getName(), library.getImportName());
            } else {
                libraries.put(library.getImportName(), library);
                nativeFunctionHandler.registerClasses(library.getNativeClasses().toArray(Class<?>[]::new));
                library.supplyNativeFunctionBindings(nativeFunctionHandler);

                autoImportFiles.addAll(library.autoImportFiles()
                        .stream()
                        .map(file -> new AutoImportFile(file, library.getImportName())).toList());
            }
        }
    }
    
    public Optional<InputStream> findLibraryByPath(String libraryName, Path path) {
        if (libraryName == null || !libraries.containsKey(libraryName)) {
            return Optional.ofNullable(getClass().getClassLoader().getResourceAsStream(path.toString()));
        }
        
        var library = libraries.get(libraryName);
        
        return library.readPath(path.toString().replace("\\", "/"));
    }

    public List<AutoImportFile> getAutoImportFiles() {
        return autoImportFiles;
    }
    
    public record AutoImportFile(String fileName, String libName) {}
}

package is.yarr.qilletni.api.lib;

import is.yarr.qilletni.api.lang.internal.NativeFunctionClassInjector;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Library {
    
    /**
     * Any files that should be imported without an explicit import statement. This may be dangerous, and is only
     * recommended for core libraries.
     * 
     * @return Any files to auto import
     */
    default List<String> autoImportFiles() {
        return Collections.emptyList();
    }

    /**
     * Gets a list of classes to register as native functions.
     * 
     * @return The list of classes to register
     */
    List<Class<?>> getNativeClasses();

    Optional<InputStream> readPath(String path);

    /**
     * Supplies any injectable native function classes to be used in the library.
     * 
     * @param nativeFunctionClassInjectorConsumer The consumer to take in Qilletni's NativeFunctionClassInjector
     */
    default void supplyNativeFunctionBindings(NativeFunctionClassInjector nativeFunctionClassInjector) {
    }
    
}

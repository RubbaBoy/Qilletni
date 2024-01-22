package is.yarr.qilletni.api.lib;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface Library {

    /**
     * Gets the name of the library.
     * 
     * @return The library's name
     */
    String getName();

    /**
     * Gets the version of the library.
     * 
     * @return The library's version
     */
    String getVersion();

    /**
     * Gets a list of classes to register as native functions.
     * 
     * @return The list of classes to register
     */
    List<Class<?>> getNativeClasses();
    
    String getImportName();

    Optional<InputStream> readPath(String path);
    
}

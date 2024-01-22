package is.yarr.qilletni.lang.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public record ImportPathState(Path path, boolean isInternal, String libraryName) {
    
    public ImportPathState(Path path, boolean isInternal) {
        this(path, isInternal, null);
    }
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportPathState.class);
    
    public static final ImportPathState VIRTUAL_STATE = new ImportPathState(Path.of(""), true);

    /**
     * Creates a new ImportPathState with a sub path in {@link #path}.
     *
     * @param childFile The name (or relative path) of the next file
     * @return The new {@link ImportPathState} to import from
     */
    public ImportPathState importFrom(String childFile) {
        childFile = childFile.substring(1, childFile.length() - 1); // strip ""

        if (childFile.startsWith("!")) {
            var colonIndex = childFile.indexOf(":");
            var libName = childFile.substring(1, colonIndex);
            var pathName = libName + "/" + childFile.substring(colonIndex + 1);
            
            LOGGER.debug("libName: {} pathName: {}", libName, pathName);
            
            return new ImportPathState(Paths.get(pathName), true, libName);
        }

        // This is not the first internal import, or else it would have begun with !
        if (isInternal) {
            var parentPath = path.getParent();
            if (parentPath == null) {
                parentPath = path;
            }
            
            return new ImportPathState(parentPath.resolve(childFile), true, libraryName);
        }

        return new ImportPathState(path.resolve(childFile), isInternal, libraryName);
    }
}

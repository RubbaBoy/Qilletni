package is.yarr.qilletni.lang.runner;

import java.nio.file.Path;
import java.nio.file.Paths;

public record ImportPathState(Path path, boolean isInternal) {
    
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
            return new ImportPathState(Paths.get(childFile.substring(1)), true);
        }

        // This is not the first internal import, or else it would have begun with !
        if (isInternal) {
            var parentPath = path.getParent();
            if (parentPath == null) {
                parentPath = path;
            }
            
            return new ImportPathState(parentPath.resolve(childFile), true);
        }

        return new ImportPathState(path.resolve(childFile), isInternal);
    }
}

package is.yarr.qilletni.lang.runner;

import java.nio.file.Path;
import java.nio.file.Paths;

public record ImportPathState(Path path, boolean isInternal) {
    /**
     * Creates a new ImportPathState with a sub path in {@link #path}.
     *
     * @param childFile The name (or relativel path) of the next file
     * @return The new {@link ImportPathState} to import from
     */
    public ImportPathState importFrom(String childFile) {
        childFile = childFile.substring(1, childFile.length() - 1); // strip ""

        if (childFile.startsWith("!")) {
            return new ImportPathState(Paths.get(childFile.substring(1)), true);
        }

        // This is not the first internal import, or else it would have begun with !
        if (isInternal) {
            return new ImportPathState(path.getParent().resolve(childFile), true);
        }

        return new ImportPathState(path.resolve(childFile), isInternal);
    }
}

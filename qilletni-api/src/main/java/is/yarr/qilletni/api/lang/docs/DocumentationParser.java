package is.yarr.qilletni.api.lang.docs;

import is.yarr.qilletni.api.lang.docs.structure.DocumentedFile;

import java.io.IOException;
import java.nio.file.Path;

public interface DocumentationParser {
    
    DocumentedFile parseDocsFromPath(Path filePath, String importPath) throws IOException;
    
    DocumentedFile parseDocsFromString(String fileContents);
    
}

package is.yarr.qilletni.lang.docs;


import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.api.lang.docs.DocumentationParser;
import is.yarr.qilletni.api.lang.docs.structure.DocumentedFile;
import is.yarr.qilletni.api.lang.docs.structure.DocumentedItem;
import is.yarr.qilletni.lang.docs.exceptions.DocErrorListener;
import is.yarr.qilletni.lang.docs.visitors.QilletniDocVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DefaultDocumentationParser implements DocumentationParser {
    
    private final String libraryName;

    public DefaultDocumentationParser(String libraryName) {
        this.libraryName = libraryName;
    }

    @Override
    public DocumentedFile parseDocsFromPath(Path filePath, String importPath) throws IOException {
        return parseDocsForCharStream(filePath.getFileName().toString(), importPath, CharStreams.fromPath(filePath));
    }

    @Override
    public DocumentedFile parseDocsFromString(String fileContents) {
        return parseDocsForCharStream(null, null, CharStreams.fromString(fileContents));
    }

    private DocumentedFile parseDocsForCharStream(String fileName, String importPath, CharStream charStream) {
        var lexer = new QilletniLexer(charStream);
        var tokens = new CommonTokenStream(lexer);
        var parser = new QilletniParser(tokens);

        lexer.addErrorListener(new DocErrorListener());
        parser.addErrorListener(new DocErrorListener());

        var qilletniDocVisitor = new QilletniDocVisitor(libraryName, importPath);
        var documentedItems = qilletniDocVisitor.visitProg(parser.prog());
        return new DocumentedFile(fileName, Objects.requireNonNullElse(documentedItems, Collections.emptyList()));
    }
}

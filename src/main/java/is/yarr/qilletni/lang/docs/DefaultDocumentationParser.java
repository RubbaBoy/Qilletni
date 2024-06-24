package is.yarr.qilletni.lang.docs;


import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.api.lang.docs.DocumentationParser;
import is.yarr.qilletni.api.lang.docs.structure.DocumentedFile;
import is.yarr.qilletni.lang.docs.exceptions.DocErrorListener;
import is.yarr.qilletni.lang.docs.visitors.QilletniDocVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Path;

public class DefaultDocumentationParser implements DocumentationParser {
    
    @Override
    public DocumentedFile parseDocsFromPath(Path filePath) throws IOException {
        return parseDocsForCharStream(filePath.getFileName().toString(), CharStreams.fromPath(filePath));
    }

    @Override
    public DocumentedFile parseDocsFromString(String fileContents) {
        return parseDocsForCharStream(null, CharStreams.fromString(fileContents));
    }

    private DocumentedFile parseDocsForCharStream(String fileName, CharStream charStream) {
        var lexer = new QilletniLexer(charStream);
        var tokens = new CommonTokenStream(lexer);
        var parser = new QilletniParser(tokens);

        lexer.addErrorListener(new DocErrorListener());
        parser.addErrorListener(new DocErrorListener());

        var qilletniDocVisitor = new QilletniDocVisitor();
        return new DocumentedFile(fileName, qilletniDocVisitor.visitProg(parser.prog()));
    }
}

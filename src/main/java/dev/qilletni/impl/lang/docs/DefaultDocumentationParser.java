package dev.qilletni.impl.lang.docs;


import dev.qilletni.impl.antlr.QilletniLexer;
import dev.qilletni.impl.antlr.QilletniParser;
import dev.qilletni.api.lang.docs.DocumentationParser;
import dev.qilletni.api.lang.docs.structure.DocumentedFile;
import dev.qilletni.impl.lang.docs.exceptions.DocErrorListener;
import dev.qilletni.impl.lang.docs.visitors.QilletniDocVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
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
        return new DocumentedFile(fileName, Paths.get(Objects.requireNonNullElse(importPath, "")), Objects.requireNonNullElse(documentedItems, Collections.emptyList()));
    }
}

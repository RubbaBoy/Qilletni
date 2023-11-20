package is.yarr.qilletni;

import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        
        new Main().main(args[0]);
    }
    
    private void main(String programFile) throws IOException {
        var input = CharStreams.fromPath(Paths.get("input", programFile));
        
        var lexer = new QilletniLexer(input);
        var tokenStream = new CommonTokenStream(lexer);
        var qilletniParser = new QilletniParser(tokenStream);
        
        QilletniParser.ProgContext programContext = qilletniParser.prog();
        var qilletniVisitor = new QilletniVisitor();
        qilletniVisitor.visit(programContext);

        System.out.println("Symbol table at the end:");

        qilletniVisitor.symbolTable.getAllScopes().forEach(scope -> {
            System.out.println(scope);
        });
    }
}
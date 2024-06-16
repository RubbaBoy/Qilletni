package is.yarr.qilletni.lang.docs;

import is.yarr.qilletni.antlr.DocsLexer;
import is.yarr.qilletni.antlr.DocsParser;
import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Paths;

public class DocParser {

    public static void main(String[] args) throws IOException {
//        String input = """
//                /**
//                 * This is the text that should be gotten
//                 * @param balls Then some description
//                 * include multi line support, only get the text though
//                 */""";
//
//        var charStream = CharStreams.fromString("");
//        var lexer = new DocsLexer(charStream);
//        var tokens = new CommonTokenStream(lexer);
//        var parser = new DocsParser(tokens);
//
//        var tree = parser.shitt();
//        var visitor = new DocVisitor();
//        var docText = visitor.visit(tree);
        
        var charStream = CharStreams.fromPath(Paths.get("E:\\Qilletni\\qilletni-lib-std\\qilletni-src\\util\\map.ql"));
        var lexer = new QilletniLexer(charStream);
        var tokens = new CommonTokenStream(lexer);
        var parser = new QilletniParser(tokens);
        
        var qilletniDocVisitor = new QilletniDocVisitor();
        var foundShit = qilletniDocVisitor.visitProg(parser.prog());

        System.out.println("foundShit = " + foundShit.size());
        
//        System.out.println("Extracted Text:");
//        System.out.println(docText);
    }
    
}

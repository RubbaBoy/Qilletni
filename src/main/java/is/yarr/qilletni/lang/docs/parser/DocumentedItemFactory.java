package is.yarr.qilletni.lang.docs.parser;

import is.yarr.qilletni.antlr.DocsLexer;
import is.yarr.qilletni.antlr.DocsParser;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.lang.docs.DocVisitor;
import is.yarr.qilletni.lang.docs.exceptions.DocErrorListener;
import is.yarr.qilletni.lang.docs.structure.DocumentedItem;
import is.yarr.qilletni.lang.docs.structure.item.DocumentedTypeEntity;
import is.yarr.qilletni.lang.docs.structure.item.DocumentedTypeField;
import is.yarr.qilletni.lang.docs.structure.item.DocumentedTypeFunction;
import is.yarr.qilletni.lang.docs.structure.text.inner.InnerDoc;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Optional;

public class DocumentedItemFactory {

    public static DocumentedItem createDocs(QilletniParser.Function_defContext ctx, String docString) {
        try {
            var innerDoc = DocVisitor.parseDoc(getDocsParser(docString), DocVisitor.DocumentingType.FUNCTION);

            var documentedType = new DocumentedTypeFunction(ctx.ID().getText(),
                    ctx.function_def_params().ID().stream().map(ParseTree::getText).toList(),
                    ctx.NATIVE() != null,
                    Optional.ofNullable(ctx.function_on_type()).map(on -> on.ID().getText()));

            return new DocumentedItem(documentedType, innerDoc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DocumentedItem createDocs(QilletniParser.Entity_defContext ctx, String docString) {
        try {
            var innerDoc = DocVisitor.parseDoc(getDocsParser(docString), DocVisitor.DocumentingType.ENTITY);

            var documentedTypeEntity = new DocumentedTypeEntity(ctx.ID().getText());

            return new DocumentedItem(documentedTypeEntity, innerDoc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DocumentedItem createDocs(QilletniParser.Entity_property_declarationContext ctx, String docString) {
        try {
            var innerDoc = DocVisitor.parseDoc(getDocsParser(docString), DocVisitor.DocumentingType.FIELD);

            var fieldName = ctx.ID(ctx.ID().size() - 1).getText();
            var documentedTypeEntity = new DocumentedTypeField(ctx.type.getText(), fieldName);

            return new DocumentedItem(documentedTypeEntity, innerDoc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static DocsParser getDocsParser(String docString) {
        var charStream = CharStreams.fromString(docString);
        var lexer = new DocsLexer(charStream);
        var tokens = new CommonTokenStream(lexer);
        var parser = new DocsParser(tokens);

        lexer.addErrorListener(new DocErrorListener());
        parser.addErrorListener(new DocErrorListener());
        
        return parser;
    }
}

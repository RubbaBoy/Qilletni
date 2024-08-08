package is.yarr.qilletni.lang.docs.visitors;

import is.yarr.qilletni.antlr.DocsLexer;
import is.yarr.qilletni.antlr.DocsParser;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.api.lang.docs.structure.item.DocumentedTypeEntityConstructor;
import is.yarr.qilletni.lang.docs.exceptions.DocErrorListener;
import is.yarr.qilletni.api.lang.docs.structure.DocumentedItem;
import is.yarr.qilletni.api.lang.docs.structure.item.DocumentedTypeEntity;
import is.yarr.qilletni.api.lang.docs.structure.item.DocumentedTypeField;
import is.yarr.qilletni.api.lang.docs.structure.item.DocumentedTypeFunction;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Optional;

public class DocumentedItemFactory {

    public static DocumentedItem createDocs(String libraryName, String fileName, QilletniParser.Function_defContext ctx, String docString) {
        try {
            var innerDoc = DocVisitor.parseDoc(getDocsParser(docString), DocVisitor.DocumentingType.FUNCTION);

            var documentedType = new DocumentedTypeFunction(libraryName, fileName, ctx.ID().getText(),
                    ctx.function_def_params().ID().stream().map(ParseTree::getText).toList(),
                    ctx.NATIVE() != null,
                    Optional.ofNullable(ctx.function_on_type()).map(on -> on.type.getText()));

            return new DocumentedItem(documentedType, innerDoc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DocumentedItem createDocs(String libraryName, String fileName, QilletniParser.Entity_defContext ctx, String docString) {
        try {
            var innerDoc = DocVisitor.parseDoc(getDocsParser(docString), DocVisitor.DocumentingType.ENTITY);

            var documentedTypeEntity = new DocumentedTypeEntity(libraryName, fileName, ctx.ID().getText());

            return new DocumentedItem(documentedTypeEntity, innerDoc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DocumentedItem createDocs(String libraryName, String fileName, QilletniParser.Entity_constructorContext ctx, String docString) {
        try {
            var innerDoc = DocVisitor.parseDoc(getDocsParser(docString), DocVisitor.DocumentingType.CONSTRUCTOR);

            var documentedTypeEntity = new DocumentedTypeEntityConstructor(libraryName, fileName, ctx.ID().getText(),
                    ctx.function_def_params().ID().stream().map(ParseTree::getText).toList());

            return new DocumentedItem(documentedTypeEntity, innerDoc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DocumentedItem createDocs(String libraryName, String fileName, QilletniParser.Entity_property_declarationContext ctx, String docString) {
        try {
            var innerDoc = DocVisitor.parseDoc(getDocsParser(docString), DocVisitor.DocumentingType.FIELD);

            var fieldName = ctx.ID(ctx.ID().size() - 1).getText();
            var documentedTypeEntity = new DocumentedTypeField(libraryName, fileName, ctx.type.getText(), fieldName);

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

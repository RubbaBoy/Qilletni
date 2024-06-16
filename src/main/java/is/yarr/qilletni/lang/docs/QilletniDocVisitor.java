package is.yarr.qilletni.lang.docs;

import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.antlr.QilletniParserBaseVisitor;
import is.yarr.qilletni.lang.docs.parser.DocumentedItemFactory;
import is.yarr.qilletni.lang.docs.structure.DocumentedItem;
import is.yarr.qilletni.lang.docs.structure.text.inner.EntityDoc;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A visitor for the main Qilletni grammar, only overriding areas that may have docs.
 * TODO: This excludes docs for file-level variables
 */
public class QilletniDocVisitor extends QilletniParserBaseVisitor<List<DocumentedItem>> {

    @Override
    public List<DocumentedItem> visitFunction_def(QilletniParser.Function_defContext ctx) {
        System.out.printf("fun %s%s(%s):%n", ctx.NATIVE() != null ? "native " : "", ctx.ID(), ctx.function_def_params().getText());

        var docComment = optionallyFormatDoc(ctx.DOC_COMMENT());

        return Collections.singletonList(DocumentedItemFactory.createDocs(ctx, docComment));
    }

    @Override
    public List<DocumentedItem> visitEntity_def(QilletniParser.Entity_defContext ctx) {
        System.out.printf("entity %s:%n", ctx.ID());

        var docComment = optionallyFormatDoc(ctx.DOC_COMMENT());
        
        var docItem = DocumentedItemFactory.createDocs(ctx, docComment);
        
        var body = ctx.entity_body();
        
        var entityDoc = ((EntityDoc) docItem.innerDoc());

        body.entity_property_declaration().forEach(propertyDef -> entityDoc.addDocItem(visit(propertyDef).get(0)));

        if (body.entity_constructor() != null) {
            entityDoc.addDocItem(visit(body.entity_constructor()).get(0));
        }
                
        body.function_def().forEach(functionDef -> entityDoc.addDocItem(visit(functionDef).get(0)));

        return Collections.singletonList(docItem);
    }

    @Override
    public List<DocumentedItem> visitEntity_property_declaration(QilletniParser.Entity_property_declarationContext ctx) {
        System.out.printf("%s %s:%n", ctx.type.getText(), ctx.ID().size() == 1 ? ctx.ID(0).getText() : ctx.ID(1).getText());

        var docComment = optionallyFormatDoc(ctx.DOC_COMMENT());

        return Collections.singletonList(DocumentedItemFactory.createDocs(ctx, docComment));
    }

    @Override
    protected List<DocumentedItem> aggregateResult(List<DocumentedItem> aggregate, List<DocumentedItem> nextResult) {
        if (aggregate == null) {
            return nextResult;
        }

        if (nextResult == null) {
            return aggregate;
        }

        var aggregated = new ArrayList<>(aggregate);
        aggregated.addAll(nextResult);
        
        return aggregated;
    }
    
    private static String optionallyFormatDoc(TerminalNode docComment) {
        if (docComment == null) {
            return "";
        }
        
        return formatDocs(docComment.getText());
    }

    private static String formatDocs(String unformattedDoc) {
        unformattedDoc = unformattedDoc.replaceAll("^/\\*\\*", "").replaceAll("\\*/$", "");
        var lines = unformattedDoc.split("\n");
        var formattedDoc = new StringBuilder();
        for (var line : lines) {
            formattedDoc.append(line.replaceAll("^\\s+\\*\\s*", "")).append("\n");
        }
        
        return formattedDoc.toString().strip();
    }
}

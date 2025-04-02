package dev.qilletni.impl.lang.docs.visitors;

import dev.qilletni.impl.antlr.QilletniParser;
import dev.qilletni.impl.antlr.QilletniParserBaseVisitor;
import dev.qilletni.api.lang.docs.structure.DocumentedItem;
import dev.qilletni.api.lang.docs.structure.text.inner.EntityDoc;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * A visitor for the main Qilletni grammar, only overriding areas that may have docs.
 * TODO: This excludes docs for file-level variables
 */
public class QilletniDocVisitor extends QilletniParserBaseVisitor<List<DocumentedItem>> {

    private final String libraryName;
    private final String fileName;

    public QilletniDocVisitor(String libraryName, String fileName) {
        this.libraryName = libraryName;
        this.fileName = fileName;
    }

    @Override
    public List<DocumentedItem> visitFunction_def(QilletniParser.Function_defContext ctx) {
        System.out.printf("fun %s%s(%s):%n", ctx.NATIVE() != null ? "native " : "", ctx.ID(), ctx.function_def_params().getText());

        var docComment = optionallyFormatDoc(ctx.DOC_COMMENT());

        return Collections.singletonList(DocumentedItemFactory.createDocs(libraryName, fileName, ctx, docComment));
    }

    @Override
    public List<DocumentedItem> visitEntity_def(QilletniParser.Entity_defContext ctx) {
        System.out.printf("entity %s:%n", ctx.ID());

        var docComment = optionallyFormatDoc(ctx.DOC_COMMENT());
        
        var docItem = DocumentedItemFactory.createDocs(libraryName, fileName, ctx, docComment);
        
        var body = ctx.entity_body();
        
        var entityDoc = ((EntityDoc) docItem.innerDoc());

        body.entity_property_declaration().stream()
                .map(this::visit)
                .filter(Predicate.not(List::isEmpty))
                .forEach(propertyDocs -> entityDoc.addDocItem(propertyDocs.getFirst()));

        System.out.println("body.entity_constructor() = " + body.entity_constructor());
        if (body.entity_constructor() != null) {
            var foundDocs = visit(body.entity_constructor());
            System.out.println("foundDocs = " + foundDocs);
            if (!foundDocs.isEmpty()) {
                entityDoc.addDocItem(foundDocs.getFirst());
            }
        }
                
        body.function_def().stream()
                .map(this::visit)
                .filter(Predicate.not(List::isEmpty))
                .forEach(functionDocs -> entityDoc.addDocItem(functionDocs.getFirst()));

        return Collections.singletonList(docItem);
    }

    @Override
    public List<DocumentedItem> visitEntity_property_declaration(QilletniParser.Entity_property_declarationContext ctx) {
        System.out.printf("%s %s:%n", ctx.type.getText(), ctx.ID().size() == 1 ? ctx.ID(0).getText() : ctx.ID(1).getText());

        var docComment = optionallyFormatDoc(ctx.DOC_COMMENT());

        return Collections.singletonList(DocumentedItemFactory.createDocs(libraryName, fileName, ctx, docComment));
    }

    @Override
    public List<DocumentedItem> visitEntity_constructor(QilletniParser.Entity_constructorContext ctx) {
        var docComment = optionallyFormatDoc(ctx.DOC_COMMENT());

        return Collections.singletonList(DocumentedItemFactory.createDocs(libraryName, fileName, ctx, docComment));
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
            if (line.trim().equals("*")) {
                formattedDoc.append("\n");
            } else {
                formattedDoc.append(line.replaceAll("^\\s+\\*\\s*", "")).append("\n");
            }
        }

        return formattedDoc.toString().strip();
    }
}

package is.yarr.qilletni.lang.docs;

import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.antlr.QilletniParserBaseVisitor;
import is.yarr.qilletni.lang.docs.parser.DocumentedItemFactory;

/**
 * A visitor for the main Qilletni grammar, only overriding areas that may have docs.
 * TODO: This excludes docs for file-level variables
 */
public class QilletniDocVisitor extends QilletniParserBaseVisitor<Object> {

    @Override
    public Object visitFunction_def(QilletniParser.Function_defContext ctx) {
        System.out.println("fun " + ctx.ID());
        if (ctx.DOC_COMMENT() != null) {
            System.out.printf("fun %s%s(%s):%n", ctx.NATIVE() != null ? "native " : "", ctx.ID(), ctx.function_def_params().getText());
//            System.out.println(formatDocs(ctx.DOC_COMMENT().getText()));
//            System.out.println("---\n");

            var docItem = DocumentedItemFactory.createDocs(ctx, formatDocs(ctx.DOC_COMMENT().getText()));
            System.out.println("docItem = " + docItem + "\n");
        }
        
        return null;
    }

    @Override
    public Object visitEntity_def(QilletniParser.Entity_defContext ctx) {
        if (ctx.DOC_COMMENT() != null) {
            System.out.printf("entity %s:%n", ctx.ID());
//            System.out.println(formatDocs(ctx.DOC_COMMENT().getText()));
//            System.out.println("---\n");
            
            var docItem = DocumentedItemFactory.createDocs(ctx, formatDocs(ctx.DOC_COMMENT().getText()));
            System.out.println("docItem = " + docItem + "\n");
        }

        return super.visitEntity_def(ctx);
    }

//    @Override
//    public Object visitEntity_constructor(QilletniParser.Entity_constructorContext ctx) {
//        if (ctx.DOC_COMMENT() != null && ctx.entity_constructor() != null) {
//            System.out.println(ctx.entity_constructor().getText() + ":");
//            System.out.println(formatDocs(ctx.DOC_COMMENT().getText()));
//            System.out.println("---\n");
//        }
//        
//        return super.visitEntity_constructor(ctx);
//    }

    @Override
    public Object visitEntity_property_declaration(QilletniParser.Entity_property_declarationContext ctx) {
        if (ctx.DOC_COMMENT() != null) {
            System.out.printf("%s %s:%n", ctx.type.getText(), ctx.ID().size() == 1 ? ctx.ID(0).getText() : ctx.ID(1).getText());
//            System.out.println(formatDocs(ctx.DOC_COMMENT().getText()));
//            System.out.println("---\n");

            var docItem = DocumentedItemFactory.createDocs(ctx, formatDocs(ctx.DOC_COMMENT().getText()));
            System.out.println("docItem = " + docItem + "\n");
        }

        return super.visitEntity_property_declaration(ctx);
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

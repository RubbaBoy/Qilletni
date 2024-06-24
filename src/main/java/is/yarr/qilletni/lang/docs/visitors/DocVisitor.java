package is.yarr.qilletni.lang.docs.visitors;

import is.yarr.qilletni.antlr.DocsParser;
import is.yarr.qilletni.antlr.DocsParserBaseVisitor;
import is.yarr.qilletni.api.lang.docs.structure.DocFieldType;
import is.yarr.qilletni.api.lang.docs.structure.text.DocDescription;
import is.yarr.qilletni.api.lang.docs.structure.text.DocErrors;
import is.yarr.qilletni.api.lang.docs.structure.text.DocOnLine;
import is.yarr.qilletni.api.lang.docs.structure.text.ParamDoc;
import is.yarr.qilletni.api.lang.docs.structure.text.ReturnDoc;
import is.yarr.qilletni.api.lang.docs.structure.text.inner.EntityDoc;
import is.yarr.qilletni.api.lang.docs.structure.text.inner.FieldDoc;
import is.yarr.qilletni.api.lang.docs.structure.text.inner.FunctionDoc;
import is.yarr.qilletni.api.lang.docs.structure.text.inner.InnerDoc;
import is.yarr.qilletni.lang.docs.exceptions.DocFormatException;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DocVisitor extends DocsParserBaseVisitor<Void> {
    
    private DocDescription docDescription;
    private final List<ParamDoc> paramDocs;
    private ReturnDoc returnDoc;
    private DocOnLine docOnLine;
    private DocFieldType docFieldType; // typeLine
    private DocErrors errorDoc;
    
    private final DocumentingType documentingType;

    private DocVisitor(DocumentingType documentingType) {
        this.documentingType = documentingType;
        this.paramDocs = new ArrayList<>();
    }
    
    public static InnerDoc parseDoc(DocsParser docsParser, DocumentingType documentingType) {
        var visitor = new DocVisitor(documentingType);
        visitor.visit(docsParser.docText());
        return documentingType.constructInnerDoc(visitor);
    }

    @Override
    public Void visitTextLine(DocsParser.TextLineContext ctx) {
        if (!documentingType.supportsDocDescription()) {
            throw new DocFormatException("Doc description not supported for this type"); // TODO: remove
        }
        
        docDescription = createDocDescription(ctx.description());
        return null;
    }

    @Override
    public Void visitParamLine(DocsParser.ParamLineContext ctx) {
        if (!documentingType.supportsParamDocs()) {
            throw new DocFormatException("Param docs not supported for this type");
        }

        var paramName = ctx.PARAM_NAME().getText();
        var paramDescription = createDocDescription(ctx.description());
        DocFieldType paramFieldType = null;
                
        var inlineBracketsContext = ctx.inline_brackets();
        if (inlineBracketsContext != null) {
            if (inlineBracketsContext.TYPE() == null) {
                throw new DocFormatException("Expected bracketed type after param");
            }

            paramFieldType = createDocFieldType(inlineBracketsContext);
        }
        
        paramDocs.add(new ParamDoc(paramName, paramFieldType, paramDescription));
        return null;
    }

    @Override
    public Void visitReturnsLine(DocsParser.ReturnsLineContext ctx) {
        if (!documentingType.supportsReturnDoc()) {
            throw new DocFormatException("Return docs not supported for this type");
        }
        
        var paramDescription = createDocDescription(ctx.description());
        DocFieldType returnType = null;
        
        var inlineBracketsContext = ctx.inline_brackets();
        if (inlineBracketsContext != null) {
            if (inlineBracketsContext.TYPE() == null) {
                throw new DocFormatException("Expected bracketed type after returns");
            }

            returnType = createDocFieldType(inlineBracketsContext);
        }
        
        returnDoc = new ReturnDoc(returnType, paramDescription);
        
        return null;
    }

    @Override
    public Void visitTypeLine(DocsParser.TypeLineContext ctx) {
        if (!documentingType.supportsFieldTypeDoc()) {
            throw new DocFormatException("Documenting a type is not supported on this type");
        }

        var fieldType = ctx.JAVA() == null ? DocFieldType.FieldType.QILLETNI : DocFieldType.FieldType.JAVA;
        docFieldType = new DocFieldType(fieldType, ctx.TEXT().getText());
        
        return null;
    }

    @Override
    public Void visitOnLine(DocsParser.OnLineContext ctx) {
        if (!documentingType.supportsOnLineDoc()) {
            throw new DocFormatException("Documenting 'on' is not supported on this type");
        }
        
        docOnLine = new DocOnLine(createDocDescription(ctx.description()));
        return null;
    }

    @Override
    public Void visitErrorsLine(DocsParser.ErrorsLineContext ctx) {
        if (!documentingType.supportsErrorDoc()) {
            throw new DocFormatException("Error docs not supported for this type");
        }
        
        errorDoc = new DocErrors(createDocDescription(ctx.description()));
        return null;
    }
    
    private DocDescription createDocDescription(DocsParser.DescriptionContext ctx) {
        var contents = new ArrayList<DocDescription.DescriptionItem>();
        for (var descriptionUnitContext : ctx.description_unit()) {
            var startText = descriptionUnitContext.TEXT().stream()
                    .map(TerminalNode::getText)
                    .map(String::strip)
                    .collect(Collectors.joining(" "));
            contents.add(new DocDescription.DocText(startText));

            var inlineBracketsContext = descriptionUnitContext.inline_brackets();
            if (inlineBracketsContext != null) {
                contents.add(createInlineRef(inlineBracketsContext));
            }
        }
        
        return new DocDescription(contents);
    }

    /**
     * Creates an object representing referencing a parameter inline in a description.
     * 
     * @param ctx The inline bracket context
     * @return The created {@link is.yarr.qilletni.api.lang.docs.structure.text.DocDescription.ParamRef}
     */
    private DocDescription.DescriptionItem createInlineRef(DocsParser.Inline_bracketsContext ctx) {
        var text = ctx.BRACKETS_TEXT().getText();
        
        if (ctx.PARAM() != null) {
            return new DocDescription.ParamRef(text);
        }

        if (ctx.JAVA() != null) {
            return new DocDescription.JavaRef(text);
        }

        if (ctx.TYPE() != null) {
            return new DocDescription.TypeRef(text);
        }

        throw new DocFormatException("Expected parameter or java type within inline description brackets");
    }

    /**
     * Creates an object representing the documentation for a parameter.
     * 
     * @param ctx The inline bracket context, that represents TYPE (not PARAM nor JAVA)
     * @return The created {@link DocFieldType}
     */
    private DocFieldType createDocFieldType(DocsParser.Inline_bracketsContext ctx) {
        if (ctx.TYPE() == null) {
            throw new IllegalStateException("Method assumes inline brackets represent TYPE, yet it does not");
        }
        
        var fieldName = ctx.BRACKETS_TEXT().getText();

        if (ctx.isJava != null) {
            return new DocFieldType(DocFieldType.FieldType.JAVA, fieldName);
        }
        
        return new DocFieldType(DocFieldType.FieldType.QILLETNI, fieldName);
    }

    // Define bit flags for different capabilities
    private static final int SUPPORTS_DOC_DESCRIPTION = 1 << 0;
    private static final int SUPPORTS_PARAM_DOCS      = 1 << 1;
    private static final int SUPPORTS_RETURN_DOC      = 1 << 2;
    private static final int SUPPORTS_ON_DOC          = 1 << 3;
    private static final int SUPPORTS_FIELD_TYPE_DOC  = 1 << 4;
    private static final int SUPPORTS_ERROR_DOC       = 1 << 5;
    
    public enum DocumentingType {
        ENTITY(SUPPORTS_DOC_DESCRIPTION) {
            @Override
            InnerDoc constructInnerDoc(DocVisitor docVisitor) {
                return new EntityDoc(docVisitor.docDescription);
            }
        },
        FIELD(SUPPORTS_DOC_DESCRIPTION | SUPPORTS_FIELD_TYPE_DOC) {
            @Override
            InnerDoc constructInnerDoc(DocVisitor docVisitor) {
                return new FieldDoc(docVisitor.docDescription, docVisitor.docFieldType);
            }
        },
        FUNCTION(SUPPORTS_DOC_DESCRIPTION
                | SUPPORTS_PARAM_DOCS
                | SUPPORTS_RETURN_DOC
                | SUPPORTS_ON_DOC
                | SUPPORTS_ERROR_DOC) {
            @Override
            InnerDoc constructInnerDoc(DocVisitor docVisitor) {
                return new FunctionDoc(docVisitor.docDescription, docVisitor.paramDocs, docVisitor.returnDoc, docVisitor.docOnLine, docVisitor.errorDoc);
            }
        };
        
        private final int capabilities;

        DocumentingType(int capabilities) {
            this.capabilities = capabilities;
        }

        abstract InnerDoc constructInnerDoc(DocVisitor docVisitor);
        
        private boolean supports(int flag) {
            return (capabilities & flag) != 0;
        }

        // Methods to check for various capabilities
        public boolean supportsDocDescription() {
            return supports(SUPPORTS_DOC_DESCRIPTION);
        }

        public boolean supportsParamDocs() {
            return supports(SUPPORTS_PARAM_DOCS);
        }

        public boolean supportsReturnDoc() {
            return supports(SUPPORTS_RETURN_DOC);
        }

        public boolean supportsOnLineDoc() {
            return supports(SUPPORTS_ON_DOC);
        }
        
        public boolean supportsFieldTypeDoc() {
            return supports(SUPPORTS_FIELD_TYPE_DOC);
        }

        public boolean supportsErrorDoc() {
            return supports(SUPPORTS_ERROR_DOC);
        }
    }
}

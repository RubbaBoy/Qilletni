package is.yarr.qilletni;

import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.antlr.QilletniParserBaseVisitor;
import is.yarr.qilletni.antlr.QilletniParserVisitor;
import is.yarr.qilletni.table.Symbol;
import is.yarr.qilletni.table.TableUtils;
import is.yarr.qilletni.types.QilletniType;
import is.yarr.qilletni.types.StringType;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Optional;

public class QilletniVisitor extends QilletniParserBaseVisitor<Object> {
    
    public final SymbolTable symbolTable = new SymbolTable();

    @Override
    public Object visitProg(QilletniParser.ProgContext ctx) {

//        for (ParseTree child : ctx.children) {
//            System.out.println("Child: " + child.getClass());
//        }
        
//        System.out.println("In program!");

//        System.out.println(ctx.toStringTree());

        visitChildren(ctx);
        
        return null;
    }

    @Override
    public Object visitFunction_def(QilletniParser.Function_defContext ctx) {
        var currScope = symbolTable.pushScope();

        System.out.println("Function def! " + ctx.getText());
        System.out.println("New scope created!");
        
        visitChildren(ctx);
        
        symbolTable.popScope();
        
        return null;
    }

    @Override
    public Object visitAsmt(QilletniParser.AsmtContext ctx) {
        System.out.println("Assignment! " + ctx.getText());

        var id = ctx.ID().getText();
//        var possibleTypes = List.of(QilletniLexer.INT_TYPE, QilletniLexer.STRING_TYPE, QilletniLexer.COLLECTION_TYPE, QilletniLexer.SONG_TYPE, QilletniLexer.WEIGHTS_KEYWORD);
//        var isDefinition = possibleTypes.contains(ctx.type.getType());
        var currentScope = symbolTable.currentScope();

        if (ctx.type != null) {
            QilletniType assignmentValue = switch (ctx.type.getType()) {
                case QilletniLexer.INT_TYPE -> {
                    System.out.println("is int!");
                    yield visitQilletniTypedNode(ctx.int_expr());
                }
                case QilletniLexer.STRING_TYPE -> {
                    System.out.println("Getting string!");
                    yield visitQilletniTypedNode(ctx.str_expr());
                }
                case QilletniLexer.COLLECTION_TYPE -> {
                    System.out.println("is coll!");
                    yield visitQilletniTypedNode(ctx.collection_expr());
                }
                case QilletniLexer.SONG_TYPE -> {
                    System.out.println("is song!");
                    yield visitQilletniTypedNode(ctx.song_expr());
                }
                case QilletniLexer.WEIGHTS_KEYWORD -> {
                    System.out.println("is weights!");
                    yield visitQilletniTypedNode(ctx.weights_expr());
                }
                default -> throw new RuntimeException("This should not be possible, unknown type");
            };

            System.out.println("Defining:\t" + id + " = " + assignmentValue);
            currentScope.define(new Symbol<>(id, Symbol.SymbolType.fromTokenType(ctx.type.getType()), assignmentValue));
        } else {
            System.out.println("Reassigning!");
            var currentSymbol = currentScope.lookup(id);

            QilletniType assignmentExpression = visitQilletniTypedNode(ctx.getChild(2));
            System.out.println("assignmentExpression = " + assignmentExpression + " for " + ctx.getChild(2).getClass());
            
            TableUtils.requireSameType(currentSymbol, assignmentExpression);
            
            currentSymbol.setValue(assignmentExpression);
            System.out.println("Reassigned " + id + " to " + assignmentExpression);

            System.out.println("currentScope = " + currentScope);
        }

        System.out.println("\tid = " + id);
        
//        visitChildren(ctx);
        return null;
    }
    
    // Expressions

    @Override
    public Object visitExpr(QilletniParser.ExprContext ctx) {
        var currentScope = symbolTable.currentScope();
        if (ctx.ID() != null) {
            System.out.println("Visiting expr! ID: " + ctx.ID());
            return currentScope.lookup(ctx.ID().getText()).getValue();
        }
        
        return visitChildren(ctx);
    }

    @Override
    public Object visitStr_expr(QilletniParser.Str_exprContext ctx) {
        System.out.println("Visit strr expr!");
        var child = ctx.getChild(0);
        
        StringType value = null;
        if (child instanceof TerminalNode terminalNode) {
            var symbol = terminalNode.getSymbol();
            var type = symbol.getType();
            if (type == QilletniLexer.ID) {
                System.out.println("Found ID! " + symbol.getText());
                value = symbolTable.currentScope().<StringType>lookup(symbol.getText()).getValue();
            }
            else if (type == QilletniLexer.STRING) {
                var stringLiteral = symbol.getText();
                value = new StringType(stringLiteral.substring(1, stringLiteral.length() - 1));
            }
        } else if (child instanceof QilletniParser.Function_callContext functionCallContext) {
            value = visitQilletniTypedNode(functionCallContext);
        }
        
        System.out.println("visiting string expression! " + value);
        return value;
    }

    @Override
    public Object visitBody_stmt(QilletniParser.Body_stmtContext ctx) {
        System.out.println("Body statement!");
        visitChildren(ctx);
        return null;
    }

    @Override
    public Object visitFunction_call(QilletniParser.Function_callContext ctx) {
        System.out.println("Calling function! " + ctx.getText());
        return super.visitFunction_call(ctx);
    }
    
    

    @Override
    public Object visitImport_file(QilletniParser.Import_fileContext ctx) {
        // TODO: Import file
        System.out.println("Importing " + ctx.FILE_NAME().getText());
        
        return null; // no children! not doing visitChildren()
    }
    
    public <T extends QilletniType> T visitQilletniTypedNode(ParseTree ctx) {
        var result = ctx.accept(QilletniVisitor.this);
        if (result == null) {
            return null;
        }
        
        return (T) result;
    }
}

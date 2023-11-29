package is.yarr.qilletni;

import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.antlr.QilletniParserBaseVisitor;
import is.yarr.qilletni.exceptions.TypeMismatchException;
import is.yarr.qilletni.table.Symbol;
import is.yarr.qilletni.table.TableUtils;
import is.yarr.qilletni.types.BooleanType;
import is.yarr.qilletni.types.FunctionType;
import is.yarr.qilletni.types.IntType;
import is.yarr.qilletni.types.QilletniType;
import is.yarr.qilletni.types.StringType;
import is.yarr.qilletni.types.TypeUtils;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class QilletniVisitor extends QilletniParserBaseVisitor<Object> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(QilletniVisitor.class);
    
    public final SymbolTable symbolTable;
    private final NativeFunctionHandler nativeFunctionHandler;
    private final Consumer<String> importConsumer;

    public QilletniVisitor(SymbolTable symbolTable, NativeFunctionHandler nativeFunctionHandler, Consumer<String> importConsumer) {
        this.symbolTable = symbolTable;
        this.nativeFunctionHandler = nativeFunctionHandler;
        this.importConsumer = importConsumer;
    }

    @Override
    public Object visitProg(QilletniParser.ProgContext ctx) {
        symbolTable.pushScope();
        visitChildren(ctx);
        return null;
    }

    @Override
    public Object visitFunction_def(QilletniParser.Function_defContext ctx) {

        var id = ctx.ID().getText();
        var currScope = symbolTable.currentScope();
        var params = (List<String>) ctx.function_def_params().accept(this);
        
        if (ctx.NATIVE() != null) {
            currScope.define(new Symbol<>(id, params.size(), FunctionType.createNativeFunction(id, params.toArray(String[]::new))));
        } else {
            currScope.define(new Symbol<>(id, params.size(), FunctionType.createImplementedFunction(id, params.toArray(String[]::new), ctx.body())));
        }
        
        return null;
    }

    @Override
    public List<String> visitFunction_def_params(QilletniParser.Function_def_paramsContext ctx) {
        return ctx.ID().stream().map(ParseTree::getText).toList();
    }

    @Override
    public Object visitAsmt(QilletniParser.AsmtContext ctx) {
        var id = ctx.ID().getText();
        var currentScope = symbolTable.currentScope();
        
        if (ctx.type != null) {
            QilletniType assignmentValue = visitQilletniTypedNode(switch (ctx.type.getType()) {
                case QilletniLexer.INT_TYPE -> ctx.int_expr();
                case QilletniLexer.BOOLEAN_TYPE -> ctx.bool_expr();
                case QilletniLexer.STRING_TYPE -> ctx.str_expr();
                case QilletniLexer.COLLECTION_TYPE -> ctx.collection_expr();
                case QilletniLexer.SONG_TYPE -> ctx.song_expr();
                case QilletniLexer.WEIGHTS_KEYWORD -> ctx.weights_expr();
                default -> throw new RuntimeException("This should not be possible, unknown type");
            });

            LOGGER.debug("(new) {} = {}", id, assignmentValue);
            currentScope.define(new Symbol<>(id, Symbol.SymbolType.fromTokenType(ctx.type.getType()), assignmentValue));
        } else {
            var currentSymbol = currentScope.lookup(id);

            QilletniType assignmentExpression = visitQilletniTypedNode(ctx.getChild(2));
            
            TableUtils.requireSameType(currentSymbol, assignmentExpression);
            
            currentSymbol.setValue(assignmentExpression);
            
            LOGGER.debug("{} = {}", id, assignmentExpression);
        }

        return null;
    }
    
    // Expressions

    @Override
    public Object visitExpr(QilletniParser.ExprContext ctx) {
        var currentScope = symbolTable.currentScope();
        if (ctx.ID() != null) {
            LOGGER.debug("Visiting expr! ID: {}", ctx.ID());
            return currentScope.lookup(ctx.ID().getText()).getValue();
        }
        
        if (ctx.LEFT_PAREN() != null) { // ( expr )
            return visitQilletniTypedNode(ctx.getChild(1));
        }
        
        return visitQilletniTypedNode(ctx.getChild(0));
    }

    @Override
    public StringType visitStr_expr(QilletniParser.Str_exprContext ctx) {
        var child = ctx.getChild(0);
        
        StringType value = null;
        if (child instanceof TerminalNode terminalNode) {
            var symbol = terminalNode.getSymbol();
            var type = symbol.getType();
            if (type == QilletniLexer.ID) {
                value = StringType.fromType(symbolTable.currentScope().<StringType>lookup(symbol.getText()).getValue());
            } else if (type == QilletniLexer.STRING) {
                var stringLiteral = symbol.getText();
                value = new StringType(stringLiteral.substring(1, stringLiteral.length() - 1));
            }
        } else if (child instanceof QilletniParser.Function_callContext functionCallContext) {
            value = visitQilletniTypedNode(functionCallContext);
        } else if (child instanceof QilletniParser.Str_exprContext) {
            value = visitQilletniTypedNode(child);
        } else if (child instanceof QilletniParser.ExprContext) {
            value = new StringType(String.valueOf(visitQilletniTypedNode(child)));
        }

        if (ctx.getChildCount() == 3) { // ( str_expr )  or  str_expr + str_expr 
            var middle = ctx.getChild(1);
            if (middle instanceof TerminalNode term && term.getSymbol().getType() == QilletniLexer.PLUS) {
                if (value == null) {
                    value = new StringType("null");
                }

                var add = visitQilletniTypedNode(ctx.getChild(2));
                value = new StringType(value.getValue() + add.stringValue());
            } else if (middle instanceof QilletniParser.Str_exprContext stringExprContext) {
                value = visitQilletniTypedNode(stringExprContext);
            }
        }
        
        return value;
    }

    @Override
    public IntType visitInt_expr(QilletniParser.Int_exprContext ctx) {
        var child = ctx.getChild(0);
        
        if (ctx.LEFT_PAREN() != null) {
            child = ctx.getChild(1);
        }

        IntType value = null;
        if (child instanceof TerminalNode terminalNode) {
            var symbol = terminalNode.getSymbol();
            var type = symbol.getType();
            if (type == QilletniLexer.ID) {
                value = symbolTable.currentScope().<IntType>lookup(symbol.getText()).getValue();
            } else if (type == QilletniLexer.INT) {
                value = new IntType(Integer.parseInt(symbol.getText()));
            }
        } else if (child instanceof QilletniParser.Function_callContext functionCallContext) {
            value = visitQilletniTypedNode(functionCallContext);
        } else if (child instanceof QilletniParser.Int_exprContext) {
            value = visitQilletniTypedNode(child);
        }
        
        if (ctx.PLUS() != null) {
            var intVal = 0;
            if (value != null) {
                intVal = value.getValue();
            }
            
            IntType add = visitQilletniTypedNode(ctx.getChild(2));
            value = new IntType(intVal + add.getValue());
        }

        return value;
    }

    @Override
    public BooleanType visitBool_expr(QilletniParser.Bool_exprContext ctx) {
        var child = ctx.getChild(0);

        BooleanType value = null;
        if (child instanceof TerminalNode terminalNode) {
            var symbol = terminalNode.getSymbol();
            var type = symbol.getType();
            if (type == QilletniLexer.ID) {
                value = symbolTable.currentScope().<BooleanType>lookup(symbol.getText()).getValue();
            } else if (type == QilletniLexer.BOOL) {
                value = new BooleanType(symbol.getText().equals("true"));
            }
        } else if (child instanceof QilletniParser.Function_callContext functionCallContext) {
            value = visitQilletniTypedNode(functionCallContext);
        } else if (ctx.REL_OP() != null && ctx.getChild(1) instanceof TerminalNode relOp) {
            var leftChild = ctx.getChild(0);
            var rightChild = ctx.getChild(2);
            var leftType = visitQilletniTypedNode(leftChild);
            var rightType = visitQilletniTypedNode(rightChild);
            
            var relOpVal = relOp.getSymbol().getText();
            
            if ("!==".contains(relOpVal)) {
                BiFunction<Comparable<?>, Comparable<?>, Boolean> compareMethod = relOpVal.equals("==") ?
                        Objects::equals : (a, b) -> !Objects.equals(a, b);
                
                if (leftChild instanceof QilletniParser.Int_exprContext && rightChild instanceof QilletniParser.Int_exprContext) {
                    var leftInt = TypeUtils.safelyCast(leftType, IntType.class).getValue();
                    var rightInt = TypeUtils.safelyCast(rightType, IntType.class).getValue();
                    
                    return new BooleanType(compareMethod.apply(leftInt, rightInt));
                } else if (leftChild instanceof QilletniParser.Bool_exprContext && rightChild instanceof QilletniParser.Bool_exprContext) {
                    var leftBool = TypeUtils.safelyCast(leftType, BooleanType.class).getValue();
                    var rightBool = TypeUtils.safelyCast(rightType, BooleanType.class).getValue();

                    return new BooleanType(compareMethod.apply(leftBool, rightBool));
                }
                
                throw new TypeMismatchException("Cannot compare differing types");
            } else {
                var leftInt = TypeUtils.safelyCast(leftType, IntType.class).getValue();
                var rightInt = TypeUtils.safelyCast(rightType, IntType.class).getValue();

                LOGGER.debug("Comparing {} {} {}", leftInt, relOpVal, rightInt);

                var comparisonResult = switch (relOpVal) {
                    case ">" -> leftInt > rightInt;
                    case "<" -> leftInt < rightInt;
                    case "<=" -> leftInt <= rightInt;
                    case ">=" -> leftInt >= rightInt;
                    default -> throw new IllegalStateException("Unexpected value: " + relOpVal);
                };
                
                return new BooleanType(comparisonResult);
            }
        }

        return value;
    }

    @Override
    public Object visitBody_stmt(QilletniParser.Body_stmtContext ctx) {
        visitChildren(ctx);
        return null;
    }

    @Override
    public Object visitFunction_call(QilletniParser.Function_callContext ctx) {
        var id = ctx.ID().getText();

        List<QilletniType> params = Collections.emptyList();
        if (ctx.expr_list() != null) {
            params = (List<QilletniType>) ctx.expr_list().accept(this);
        }
                
        var functionType = symbolTable.currentScope().<FunctionType>lookup(id).getValue();
        if (functionType.isNative()) {
            LOGGER.debug("Invoking native! {}", functionType.getName());
            return nativeFunctionHandler.invokeNativeMethod(functionType.getName(), params);
        }
        
        symbolTable.pushScope();

        var result = visitQilletniTypedNode(functionType.getBodyContext());
        
        symbolTable.popScope();
        return result;
    }

    @Override
    public List<QilletniType> visitExpr_list(QilletniParser.Expr_listContext ctx) {
        return ctx.expr().stream()
                .<QilletniType>map(this::visitQilletniTypedNode)
                .toList();
    }

    @Override
    public QilletniType visitReturn_stmt(QilletniParser.Return_stmtContext ctx) {
        return visitQilletniTypedNode(ctx.expr());
    }

    @Override
    public Object visitIf_stmt(QilletniParser.If_stmtContext ctx) {
        BooleanType conditional = visitQilletniTypedNode(ctx.bool_expr());
        if (conditional.getValue()) {
            visitQilletniTypedNode(ctx.body());
            return null;
        } else if (ctx.elseif_list() != null) { // for properly getting return val, we need to know both if this was invoked AND if it ran, right? OR, keep the result on the scope
            // result is if it went through (any if conditional was true)
            BooleanType elseIfList = visitQilletniTypedNode(ctx.elseif_list());
            if (elseIfList.getValue()) {
                return null;
            }
        }
        
        if (ctx.else_body() != null) {
            visitQilletniTypedNode(ctx.else_body());
            return null;
        }
        
        return null;
    }

    @Override
    public BooleanType visitElseif_list(QilletniParser.Elseif_listContext ctx) {
        if (ctx.ELSE_KEYWORD() == null) { // epsilon
            return BooleanType.FALSE;
        }
        
        BooleanType conditional = visitQilletniTypedNode(ctx.bool_expr());
        if (conditional.getValue()) {
            visitQilletniTypedNode(ctx.body());
            return BooleanType.TRUE;
        } else if (ctx.elseif_list() != null) {
            return visitQilletniTypedNode(ctx.elseif_list());
        }
        
        return BooleanType.FALSE;
    }

    @Override
    public BooleanType visitElse_body(QilletniParser.Else_bodyContext ctx) {
        if (ctx.ELSE_KEYWORD() == null) { // epsilon
            return BooleanType.FALSE;
        }
        
        visitQilletniTypedNode(ctx.body());
        return BooleanType.TRUE;
    }

    @Override
    public Object visitImport_file(QilletniParser.Import_fileContext ctx) {
        importConsumer.accept(ctx.STRING().getText());
        return null;
    }
    
    public <T extends QilletniType> T visitQilletniTypedNode(ParseTree ctx) {
        var result = ctx.accept(QilletniVisitor.this);
        if (result == null) {
            return null;
        }
        
        return (T) result;
    }
}

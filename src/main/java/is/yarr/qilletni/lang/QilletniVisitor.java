package is.yarr.qilletni.lang;

import is.yarr.qilletni.StringUtility;
import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.antlr.QilletniParserBaseVisitor;
import is.yarr.qilletni.lang.exceptions.AlreadyDefinedException;
import is.yarr.qilletni.lang.exceptions.InvalidParameterException;
import is.yarr.qilletni.lang.exceptions.TypeMismatchException;
import is.yarr.qilletni.lang.table.Symbol;
import is.yarr.qilletni.lang.table.TableUtils;
import is.yarr.qilletni.lang.types.BooleanType;
import is.yarr.qilletni.lang.types.CollectionType;
import is.yarr.qilletni.lang.types.FunctionType;
import is.yarr.qilletni.lang.types.IntType;
import is.yarr.qilletni.lang.types.QilletniType;
import is.yarr.qilletni.lang.types.SongType;
import is.yarr.qilletni.lang.types.StringType;
import is.yarr.qilletni.lang.types.TypeUtils;
import is.yarr.qilletni.lang.types.WeightsType;
import is.yarr.qilletni.lang.types.collection.CollectionLimit;
import is.yarr.qilletni.lang.types.collection.CollectionLimitUnit;
import is.yarr.qilletni.lang.types.weights.WeightEntry;
import is.yarr.qilletni.lang.types.weights.WeightUnit;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class QilletniVisitor extends QilletniParserBaseVisitor<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QilletniVisitor.class);

    public final SymbolTable symbolTable;
    private final NativeFunctionHandler nativeFunctionHandler;
    private final Consumer<String> importConsumer;

    /**
     * Populated when a method is being invoked upon an expression. For example, with the following expression:
     * foo.bar()
     * 
     * foo is evaluated, and pushed to this stack. Then, bar() is evaluated and then pops off the value. 
     */
    private final Stack<QilletniType> invokedUponExpressionStack = new Stack<>();

    public QilletniVisitor(SymbolTable symbolTable, NativeFunctionHandler nativeFunctionHandler, Consumer<String> importConsumer) {
        this.symbolTable = symbolTable;
        this.nativeFunctionHandler = nativeFunctionHandler;
        this.importConsumer = importConsumer;
    }

    @Override
    public Object visitProg(QilletniParser.ProgContext ctx) {
        symbolTable.initScope();
        visitChildren(ctx);
        return null;
    }

    @Override
    public Object visitFunction_def(QilletniParser.Function_defContext ctx) {

        var id = ctx.ID().getText();
        var currScope = symbolTable.currentScope();
        var params = new ArrayList<>((List<String>) ctx.function_def_params().accept(this));
        Class<? extends QilletniType> onType = null;
        
        if (ctx.function_on_type() != null) {
            onType = visitNode(ctx.function_on_type());
        }

        if (ctx.NATIVE() != null) {
//            if (onType != null) {
//                params.remove(0);
//            }
            
            currScope.define(new Symbol<>(id, params.size(), FunctionType.createNativeFunction(id, params.toArray(String[]::new), onType)));
        } else {
            currScope.define(new Symbol<>(id, params.size(), FunctionType.createImplementedFunction(id, params.toArray(String[]::new), onType, ctx.body())));
        }

        return null;
    }

    @Override
    public List<String> visitFunction_def_params(QilletniParser.Function_def_paramsContext ctx) {
        return ctx.ID().stream().map(ParseTree::getText).toList();
    }

    @Override
    public Class<? extends QilletniType> visitFunction_on_type(QilletniParser.Function_on_typeContext ctx) {
        return TypeUtils.getTypeFromString(ctx.type.getText());
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
    public QilletniType visitExpr(QilletniParser.ExprContext ctx) {
        var currentScope = symbolTable.currentScope();
        if (ctx.ID().size() == 1) {
            LOGGER.debug("Visiting expr! ID: {}", ctx.ID());
            return currentScope.lookup(ctx.ID(0).getText()).getValue();
        }

        if (ctx.LEFT_PAREN() != null) { // ( expr )
            return visitQilletniTypedNode(ctx.getChild(1));
        }
        
        if (ctx.ID().size() == 2) { // id + id
            var leftId = currentScope.lookup(ctx.ID(0).getText()).getValue();
            var rightId = currentScope.lookup(ctx.ID(1).getText()).getValue();
            
            if (leftId instanceof StringType leftString) {
                var leftValue = leftString.getValue();
                
                Object rightAppending = "";
                if (rightId instanceof IntType rightInt) {
                    rightAppending = rightInt.getValue();
                } else if (rightId instanceof StringType rightString) {
                    rightAppending = rightString.getValue();
                }
                
                return new StringType(leftValue + rightAppending);
            } else if (leftId instanceof IntType leftInt) {
                var leftValue = leftInt.getValue();
                
                if (rightId instanceof IntType rightInt) {
                    var rightValue = rightInt.getValue();
                    return new IntType(leftValue + rightValue);
                } else if (rightId instanceof StringType rightString) {
                    var rightValue = rightString.getValue();
                    return new StringType(leftValue + rightValue);
                }
            }
        }
        
        if (ctx.DOT() != null) { // foo.bar()
            var leftExpr = visitQilletniTypedNode(ctx.expr());
            invokedUponExpressionStack.push(leftExpr);
            return visitQilletniTypedNode(ctx.function_call());
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
        
        if (ctx.OP() != null || ctx.PLUS() != null) {
            var operation = ctx.OP() != null ? ctx.OP().getText() : "+";
            BiFunction<Integer, Integer, Integer> calculate = switch (operation) {
                case "*" -> (a, b) -> a * b;
                case "/" -> (a, b) -> a / b;
                case "%" -> (a, b) -> a % b;
                case "+" -> (a, b) -> a + b;
                case "-" -> (a, b) -> a - b;
                default -> throw new IllegalStateException("Unexpected value: " + ctx.OP().getText());
            };

            var intVal = 0;
            if (value != null) {
                intVal = value.getValue();
            }

            IntType add = visitQilletniTypedNode(ctx.getChild(2));
            value = new IntType(calculate.apply(intVal, add.getValue()));
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
    public Object visitStmt(QilletniParser.StmtContext ctx) {
        if (ctx.DOT() != null) { // foo.bar()
            var leftExpr = visitQilletniTypedNode(ctx.expr());
            invokedUponExpressionStack.push(leftExpr);
            return visitQilletniTypedNode(ctx.function_call());
        }
        
        return visitChildren(ctx);
    }

    @Override
    public Object visitFunction_call(QilletniParser.Function_callContext ctx) {
        var id = ctx.ID().getText();

        List<QilletniType> params = new ArrayList<>();
        if (ctx.expr_list() != null) {
            params.addAll(visitNode(ctx.expr_list()));
        }

        var functionType = symbolTable.currentScope().<FunctionType>lookup(id).getValue();

        var functionParams = new ArrayList<>(Arrays.asList(functionType.getParams()));
        
        var invokingUpon = functionType.getOnType() != null;
        
        var expectedParamLength = functionParams.size();
        if (invokingUpon) {
            expectedParamLength--;
        }
        
        if (expectedParamLength != params.size()) {
            throw new InvalidParameterException("Expected " + expectedParamLength + " parameters, got " + params.size());
        }

        Class<? extends QilletniType> invokingUponExpressionType = null;
        if (invokingUpon) {
            var expr = invokedUponExpressionStack.pop();
            params.add(expr);
            invokingUponExpressionType = expr.getClass();
        }
        
        if (functionType.isNative()) {
            LOGGER.debug("Invoking native! {}", functionType.getName());
            return nativeFunctionHandler.invokeNativeMethod(functionType.getName(), params, invokingUponExpressionType);
        }

        var functionScope = symbolTable.functionCall();
        
        for (int i = 0; i < params.size(); i++) {
            var qilletniType = params.get(i);
            functionScope.define(new Symbol<>(functionParams.get(i), Symbol.SymbolType.fromQilletniType(qilletniType.getClass()), qilletniType));
        }
        
        var result = visitQilletniTypedNode(functionType.getBodyContext());

        symbolTable.endFunctionCall();
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
    public Object visitFor_stmt(QilletniParser.For_stmtContext ctx) {
        var scope = symbolTable.pushScope();
        
        var range = ctx.for_expr().range();
        if (range != null) {
            var id = range.ID().getText();
            if (scope.isDefined(id)) {
                throw new AlreadyDefinedException("Symbol " + id + " has already been defined!");
            }
        }
        
        while (this.<BooleanType>visitQilletniTypedNode(ctx.for_expr()).getValue()) {
            visitQilletniTypedNode(ctx.body());
        }
        
        symbolTable.popScope();
        
        return null;
    }

    /**
     * @param ctx the parse tree
     * @return If the loop should iterate
     */
    @Override
    public BooleanType visitFor_expr(QilletniParser.For_exprContext ctx) {
        if (ctx.bool_expr() != null) {
            return visitQilletniTypedNode(ctx.bool_expr());
        } else if (ctx.range() != null) {
            return visitQilletniTypedNode(ctx.range());
        }
        
        // Should never happen
        return BooleanType.FALSE;
    }

    @Override
    public Object visitRange(QilletniParser.RangeContext ctx) {
        var scope = symbolTable.currentScope();
        var id = ctx.ID().getText();

        var rangeTo = ctx.RANGE_INFINITY() != null ? Integer.MAX_VALUE : Integer.parseInt(ctx.getChild(2).getText());

        if (!scope.isDefined(id)) { // first iteration, let it pass
            scope.define(new Symbol<>(id, Symbol.SymbolType.INT, new IntType(0)));
            return BooleanType.TRUE;
        }

        var idIntType = scope.<IntType>lookup(id);
        var newValue = idIntType.getValue().getValue() + 1;
        idIntType.setValue(new IntType(newValue));
        
        if (rangeTo > newValue) {
            return BooleanType.TRUE;
        }

        return BooleanType.FALSE;
    }

    @Override
    public Object visitImport_file(QilletniParser.Import_fileContext ctx) {
        importConsumer.accept(ctx.STRING().getText());
        return null;
    }
    
    // Song-related things

    @Override
    public SongType visitSong_expr(QilletniParser.Song_exprContext ctx) {
        var scope = symbolTable.currentScope();
        
        if (ctx.ID() != null) {
            return scope.<SongType>lookup(ctx.ID().getText()).getValue();
        }
        
        if (ctx.function_call() != null) {
            return visitQilletniTypedNode(ctx.function_call());
        }

        var urlOrName = ctx.url_or_name_pair();
        if (urlOrName.STRING().size() == 1) {
            return new SongType(StringUtility.removeQuotes(urlOrName.STRING(0).getText()));
        }

        return new SongType(StringUtility.removeQuotes(urlOrName.STRING(0).getText()), StringUtility.removeQuotes(urlOrName.STRING(1).getText()));
    }

    @Override
    public Void visitUrl_or_name_pair(QilletniParser.Url_or_name_pairContext ctx) {
        throw new RuntimeException("This should never be visited!");
    }

    @Override
    public WeightsType visitWeights_expr(QilletniParser.Weights_exprContext ctx) {
        var weights = ctx.single_weight().stream().map(this::<WeightEntry>visitNode).toList();
        return new WeightsType(weights);
    }

    @Override
    public WeightEntry visitSingle_weight(QilletniParser.Single_weightContext ctx) {
        var weightAmount = ctx.weight_amount();
        var weightInt = Integer.parseInt(weightAmount.INT().getText());
        SongType song = visitQilletniTypedNode(ctx.song_expr());
        
        return new WeightEntry(weightInt, WeightUnit.fromSymbol(weightAmount.WEIGHT_UNIT().getText()), song);
    }

    @Override
    public Object visitPlay_stmt(QilletniParser.Play_stmtContext ctx) {
        if (ctx.song_expr() != null) {
            SongType song = visitQilletniTypedNode(ctx.song_expr());
            LOGGER.debug("Playing {}", song);
            return null;
        }
        
        var collection = visitQilletniTypedNode(ctx.collection_expr());
        
        if (ctx.collection_limit() != null) {
            CollectionLimit limit = visitNode(ctx.collection_limit());
            LOGGER.debug("Playing collection {} with a limit of {}", collection, limit);
        } else {
            LOGGER.debug("Playing collection {}", collection);
        }
        
        return null;
    }

    @Override
    public Object visitCollection_expr(QilletniParser.Collection_exprContext ctx) {
        var scope = symbolTable.currentScope();

        if (ctx.ID() != null) {
            return scope.<CollectionType>lookup(ctx.ID().getText()).getValue();
        }

        if (ctx.function_call() != null) {
            return visitQilletniTypedNode(ctx.function_call());
        }

        var urlOrName = ctx.url_or_name_pair();
        if (urlOrName.STRING().size() == 1) {
            return new CollectionType(StringUtility.removeQuotes(urlOrName.STRING(0).getText()));
        }

        return new CollectionType(StringUtility.removeQuotes(urlOrName.STRING(0).getText()), StringUtility.removeQuotes(urlOrName.STRING(1).getText()));
    }

    @Override
    public Object visitOrder_define(QilletniParser.Order_defineContext ctx) {
        return super.visitOrder_define(ctx);
    }

    @Override
    public Object visitWeights_define(QilletniParser.Weights_defineContext ctx) {
        return super.visitWeights_define(ctx);
    }

    @Override
    public Object visitWeight_amount(QilletniParser.Weight_amountContext ctx) {
        return super.visitWeight_amount(ctx);
    }

    @Override
    public CollectionLimit visitCollection_limit(QilletniParser.Collection_limitContext ctx) {
        return visitNode(ctx.limit_amount());
    }

    @Override
    public CollectionLimit visitLimit_amount(QilletniParser.Limit_amountContext ctx) {
        var limitUnit = CollectionLimitUnit.COUNT;
        if (ctx.LIMIT_UNIT() != null) {
            limitUnit = CollectionLimitUnit.fromText(ctx.LIMIT_UNIT().getText());
        }
        
        return new CollectionLimit(Integer.parseInt(ctx.INT().getText()), limitUnit);
    }

    public <T extends QilletniType> T visitQilletniTypedNode(ParseTree ctx) {
        return this.visitNode(ctx);
    }

    public <T> T visitNode(ParseTree ctx) {
        var result = ctx.accept(QilletniVisitor.this);
        if (result == null) {
            return null;
        }

        return (T) result;
    }
}

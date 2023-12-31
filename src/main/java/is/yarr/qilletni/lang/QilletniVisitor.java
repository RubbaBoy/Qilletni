package is.yarr.qilletni.lang;

import is.yarr.qilletni.StringUtility;
import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.antlr.QilletniParserBaseVisitor;
import is.yarr.qilletni.lang.exceptions.AlreadyDefinedException;
import is.yarr.qilletni.lang.exceptions.FunctionDidntReturnException;
import is.yarr.qilletni.lang.exceptions.InvalidConstructor;
import is.yarr.qilletni.lang.exceptions.InvalidParameterException;
import is.yarr.qilletni.lang.exceptions.ListOutOfBoundsException;
import is.yarr.qilletni.lang.exceptions.TypeMismatchException;
import is.yarr.qilletni.lang.internal.NativeFunctionHandler;
import is.yarr.qilletni.lang.table.Scope;
import is.yarr.qilletni.lang.table.Symbol;
import is.yarr.qilletni.lang.table.SymbolTable;
import is.yarr.qilletni.lang.table.TableUtils;
import is.yarr.qilletni.lang.types.BooleanType;
import is.yarr.qilletni.lang.types.CollectionType;
import is.yarr.qilletni.lang.types.EntityType;
import is.yarr.qilletni.lang.types.FunctionType;
import is.yarr.qilletni.lang.types.IntType;
import is.yarr.qilletni.lang.types.ListType;
import is.yarr.qilletni.lang.types.QilletniType;
import is.yarr.qilletni.lang.types.TypelessListType;
import is.yarr.qilletni.lang.types.collection.CollectionOrder;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.lang.types.SongType;
import is.yarr.qilletni.lang.types.StringType;
import is.yarr.qilletni.lang.types.TypeUtils;
import is.yarr.qilletni.lang.types.WeightsType;
import is.yarr.qilletni.lang.types.collection.CollectionLimit;
import is.yarr.qilletni.lang.types.collection.CollectionLimitUnit;
import is.yarr.qilletni.lang.types.entity.EntityAttributes;
import is.yarr.qilletni.lang.types.entity.EntityDefinition;
import is.yarr.qilletni.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.lang.types.entity.UninitializedType;
import is.yarr.qilletni.lang.types.weights.WeightEntry;
import is.yarr.qilletni.lang.types.weights.WeightUnit;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QilletniVisitor extends QilletniParserBaseVisitor<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QilletniVisitor.class);

    private final SymbolTable symbolTable;
    private final Scope globalScope;
    private final EntityDefinitionManager entityDefinitionManager;
    private final NativeFunctionHandler nativeFunctionHandler;
    private final Consumer<String> importConsumer;

    public QilletniVisitor(SymbolTable symbolTable, Scope globalScope, EntityDefinitionManager entityDefinitionManager, NativeFunctionHandler nativeFunctionHandler, Consumer<String> importConsumer) {
        this.symbolTable = symbolTable;
        this.globalScope = globalScope;
        this.entityDefinitionManager = entityDefinitionManager;
        this.nativeFunctionHandler = nativeFunctionHandler;
        this.importConsumer = importConsumer;
    }

    @Override
    public Object visitProg(QilletniParser.ProgContext ctx) {
        symbolTable.initScope(globalScope);
        visitChildren(ctx);
        return null;
    }

    @Override
    public Object visitFunction_def(QilletniParser.Function_defContext ctx) {
        var currScope = symbolTable.currentScope();
        scopedVisitFunctionDef(currScope, ctx);
        return null;
    }
    
    private void scopedVisitFunctionDef(Scope scope, QilletniParser.Function_defContext ctx) {
        var id = ctx.ID().getText();
        
        var params = new ArrayList<String>(visitNode(ctx.function_def_params()));
        QilletniTypeClass<?> onType = null;

        if (ctx.function_on_type() != null) {
            onType = visitNode(ctx.function_on_type());
        }

        if (ctx.NATIVE() != null) {
            scope.defineFunction(Symbol.createFunctionSymbol(id, params.size(), FunctionType.createNativeFunction(id, params.toArray(String[]::new), onType)));
        } else {
            scope.defineFunction(Symbol.createFunctionSymbol(id, params.size(), FunctionType.createImplementedFunction(id, params.toArray(String[]::new), onType, ctx.body())));
        }
    }

    @Override
    public List<String> visitFunction_def_params(QilletniParser.Function_def_paramsContext ctx) {
        return ctx.ID().stream().map(ParseTree::getText).toList();
    }

    @Override
    public QilletniTypeClass<?> visitFunction_on_type(QilletniParser.Function_on_typeContext ctx) {
        return TypeUtils.getTypeFromStringOrThrow(ctx.type.getText());
    }

    @Override
    public Object visitAsmt(QilletniParser.AsmtContext ctx) {
        var id = ctx.ID(ctx.ID().size() - 1).getText();
        
        var currentScope = symbolTable.currentScope();

        if (ctx.type != null && ctx.LEFT_SBRACKET() != null) { // defining a new list
            var expr = ctx.expr(0);
            ListType assignmentValue = switch (ctx.type.getType()) {
                case QilletniLexer.INT_TYPE -> createQilletniList(expr, QilletniTypeClass.INT);
                case QilletniLexer.BOOLEAN_TYPE -> createQilletniList(expr, QilletniTypeClass.BOOLEAN);
                case QilletniLexer.STRING_TYPE -> createQilletniList(expr, QilletniTypeClass.STRING);
                case QilletniLexer.COLLECTION_TYPE -> createQilletniList(expr, QilletniTypeClass.COLLECTION);
                case QilletniLexer.SONG_TYPE -> createQilletniList(expr, QilletniTypeClass.SONG);
                case QilletniLexer.WEIGHTS_KEYWORD -> createQilletniList(expr, QilletniTypeClass.WEIGHTS);
                case QilletniLexer.ID -> {
                    var entityName = ctx.type.getText();

                    var expectedEntity = entityDefinitionManager.lookup(entityName).getQilletniTypeClass();
                    var entityNode = createQilletniList(expr, QilletniTypeClass.createEntityTypePlaceholder(entityName));

                    var listSubtypeClass = entityNode.getSubType();
                    if (!listSubtypeClass.equals(expectedEntity)) {
                        var gotTypeName = listSubtypeClass.getEntityDefinition().getTypeName();
                        throw new TypeMismatchException("Expected entity " + entityName + ", got " + gotTypeName);
                    }

                    yield entityNode;
                }
                default -> throw new RuntimeException("This should not be possible, unknown type");
            };

            LOGGER.debug("(new) {}[] = {}", id, assignmentValue);
            currentScope.define(Symbol.createGenericSymbol(id, assignmentValue.getTypeClass(), assignmentValue));
            
            // visitQilletniList
        } else if (ctx.LEFT_SBRACKET() != null) { // foo[123] = expr
            var listSymbol = currentScope.<ListType>lookup(id);
            var list = listSymbol.getValue();
            var index = visitQilletniTypedNode(ctx.int_expr(), IntType.class).getValue();

            if (index < 0 || index > list.getItems().size()) {
                throw new ListOutOfBoundsException("Attempted to access index " + index + " on a list with a size of " + list.getItems().size());
            }
            
            var expressionValue = visitQilletniTypedNode(ctx.expr(0));
            if (!expressionValue.getTypeClass().equals(list.getSubType())) {
                throw new TypeMismatchException("Attempted to assign a " + expressionValue.typeName() + " in a " + list.typeName() + " list");
            }

            var mutableItems = new ArrayList<>(list.getItems());
            mutableItems.set(index, expressionValue);
            list.setItems(mutableItems);
            
            listSymbol.setValue(list); // Not really needed
        } else if (ctx.type != null) { // defining a new var
            var expr = ctx.expr(0);
            QilletniType assignmentValue = switch (ctx.type.getType()) {
                case QilletniLexer.INT_TYPE -> visitQilletniTypedNode(expr, IntType.class);
                case QilletniLexer.BOOLEAN_TYPE -> visitQilletniTypedNode(expr, BooleanType.class);
                case QilletniLexer.STRING_TYPE -> visitQilletniTypedNode(expr, StringType.class);
                case QilletniLexer.COLLECTION_TYPE -> {
                    var value = visitQilletniTypedNode(expr);
                    if (!(value instanceof StringType stringType)) {
                        yield TypeUtils.safelyCast(value, CollectionType.class);
                    }

                    yield new CollectionType(stringType.stringValue());
                }
                case QilletniLexer.SONG_TYPE -> {
                    var value = visitQilletniTypedNode(expr);
                    if (!(value instanceof StringType stringType)) {
                        yield TypeUtils.safelyCast(value, SongType.class);
                    }

                    yield new SongType(stringType.stringValue());
                }
                case QilletniLexer.WEIGHTS_KEYWORD -> visitQilletniTypedNode(expr, WeightsType.class);
                case QilletniLexer.ID -> {
                    var entityName = ctx.type.getText();
                    
                    var expectedEntity = entityDefinitionManager.lookup(entityName);
                    var entityNode = visitQilletniTypedNode(expr, EntityType.class);
                    
                    var gotTypeName = entityNode.getEntityDefinition().getTypeName();
                    if (!entityNode.getEntityDefinition().equals(expectedEntity)) {
                        throw new TypeMismatchException("Expected entity " + entityName + ", got " + gotTypeName);
                    }
                    
                    yield entityNode; 
                }
                default -> throw new RuntimeException("This should not be possible, unknown type");
            };
            
            LOGGER.debug("(new) {} = {}", id, assignmentValue);
            currentScope.define(Symbol.createGenericSymbol(id, assignmentValue.getTypeClass(), assignmentValue));
        } else if (ctx.expr_assign != null) {
            var entity = visitQilletniTypedNode(ctx.expr(0), EntityType.class);
            var propertyValue = visitQilletniTypedNode(ctx.expr(1));
            var entityScope = entity.getEntityScope();
            entityScope.lookup(id).setValue(propertyValue);
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
            var idText = ctx.ID(0).getText();
            
            if (ctx.LEFT_SBRACKET() != null) { // foo[123]
                var list = currentScope.<ListType>lookup(idText).getValue().getItems();
                var index = visitQilletniTypedNode(ctx.int_expr(), IntType.class).getValue();
                
                if (index < 0 || index > list.size()) {
                    throw new ListOutOfBoundsException("Attempted to access index " + index + " on a list with a size of " + list.size());
                }
                
                return list.get(index);
            }
            
            if (ctx.DOT() == null) { // id
                LOGGER.debug("Visiting expr! ID: {}", ctx.ID());
                return currentScope.lookup(idText).getValue();
            } else { // foo.baz
                var entity = visitQilletniTypedNode(ctx.expr(), EntityType.class);
                LOGGER.debug("Getting property {} on entity {}", idText, entity.typeName());
                var entityScope = entity.getEntityScope();
                return entityScope.lookup(idText).getValue();
            }
        }

        if (ctx.DOT() != null) { // foo.bar()
            var leftExpr = visitQilletniTypedNode(ctx.expr());
            return visitFunctionCallWithContext(ctx.function_call(), leftExpr).orElseThrow(FunctionDidntReturnException::new);
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
        
        if (ctx.function_call() != null) {
            return visitFunctionCallWithContext(ctx.function_call()).orElseThrow(FunctionDidntReturnException::new);
        }

        return visitQilletniTypedNode(ctx.getChild(0), QilletniType.class);
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
            value = this.<StringType>visitFunctionCallWithContext(functionCallContext).orElseThrow(FunctionDidntReturnException::new);
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
            value = this.<IntType>visitFunctionCallWithContext(functionCallContext).orElseThrow(FunctionDidntReturnException::new);;
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
            value = this.<BooleanType>visitFunctionCallWithContext(functionCallContext).orElseThrow(FunctionDidntReturnException::new);;
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
    public ListType visitList_expression(QilletniParser.List_expressionContext ctx) {
        if (ctx.ID() != null) {
            var scope = symbolTable.currentScope();
            return scope.<ListType>lookup(ctx.ID().getText()).getValue();
        }
        
        if (ctx.expr_list() == null) {
            return new TypelessListType();
        }

        var items = this.<List<QilletniType>>visitNode(ctx.expr_list());
        
        var typeList = items.stream().map(QilletniType::getTypeClass).distinct().toList();
        if (typeList.size() > 1) {
            throw new TypeMismatchException("Multiple types found in list");
        }
        
        return new ListType(typeList.get(0), items);
    }

    @Override
    public Optional<QilletniType> visitBody(QilletniParser.BodyContext ctx) {
        if (ctx.return_stmt() != null) {
            return Optional.of(visitQilletniTypedNode(ctx.return_stmt()));
        }
        
        if (ctx.body_stmt() != null) {
            Optional<QilletniType> res = visitNode(ctx.body_stmt());
            return Optional.ofNullable(res.orElseGet(() -> this.<Optional<QilletniType>>visitNode(ctx.body()).orElse(null)));
        }
        
        return Optional.empty();
    }

    @Override
    public Optional<QilletniType> visitBody_stmt(QilletniParser.Body_stmtContext ctx) {
        if (ctx.if_stmt() != null) {
            return visitNode(ctx.if_stmt());
        }
        
        if (ctx.for_stmt() != null) {
            return visitNode(ctx.for_stmt());
        }

        visitNode(ctx.stmt());
        return Optional.empty();
    }

    @Override
    public Object visitStmt(QilletniParser.StmtContext ctx) {
        if (ctx.DOT() != null) { // foo.bar()
            var leftExpr = visitQilletniTypedNode(ctx.expr());
            visitFunctionCallWithContext(ctx.function_call(), leftExpr);
            return null;
        }
        
        visitChildren(ctx);
        return null;
    }
    
    private <T extends QilletniType> Optional<T> visitFunctionCallWithContext(QilletniParser.Function_callContext ctx) {
        return visitFunctionCallWithContext(ctx, null);
    }
    
    private <T extends QilletniType> Optional<T> visitFunctionCallWithContext(QilletniParser.Function_callContext ctx, QilletniType invokedOn) {
        var id = ctx.ID().getText();

        LOGGER.debug("invokedOn = {}", invokedOn);
        var swappedScope = false;
        var hasOnType = invokedOn != null;
        if (hasOnType && invokedOn instanceof EntityType entityType) {
            symbolTable.swapScope(entityType.getEntityScope());
            swappedScope = true;
            hasOnType = false;
        }
        
        var scope = symbolTable.currentScope();

        List<QilletniType> params = new ArrayList<>();
        if (ctx.expr_list() != null) {
            params.addAll(visitNode(ctx.expr_list()));
        }
        
        var functionType = scope.lookupFunction(id, params.size()).getValue();
        
        if (hasOnType && !functionType.getOnType().equals(invokedOn.getTypeClass())) {
            throw new RuntimeException("Function not to be invoked on " + invokedOn.getTypeClass() + " should be " + functionType.getOnType());
        }

        var functionParams = new ArrayList<>(Arrays.asList(functionType.getParams()));

        var expectedParamLength = functionParams.size();
        if (hasOnType) {
            expectedParamLength--;
        }

        if (expectedParamLength != params.size()) {
            throw new InvalidParameterException("Expected " + expectedParamLength + " parameters, got " + params.size());
        }

        QilletniTypeClass<?> invokingUponExpressionType = null;
        if (hasOnType) {
            params.add(0, invokedOn);
            invokingUponExpressionType = invokedOn.getTypeClass();
        }

        if (functionType.isNative()) {
            LOGGER.debug("Invoking native! {}", functionType.getName());
            return Optional.ofNullable((T) nativeFunctionHandler.invokeNativeMethod(functionType.getName(), params, invokingUponExpressionType));
        }

        var functionScope = symbolTable.functionCall();

        for (int i = 0; i < params.size(); i++) {
            var qilletniType = params.get(i);
            functionScope.define(Symbol.createGenericSymbol(functionParams.get(i), TypeUtils.getTypeFromInternalType(qilletniType.getClass()), qilletniType));
        }

        Optional<T> result = visitNode(functionType.getBodyContext());

        symbolTable.endFunctionCall();
        
        if (swappedScope) {
            symbolTable.unswapScope();
        }
        
        return result;
    }

    @Override
    public Optional<QilletniType> visitFunction_call(QilletniParser.Function_callContext ctx) {
        return visitFunctionCallWithContext(ctx);
    }

    @Override
    public List<QilletniType> visitExpr_list(QilletniParser.Expr_listContext ctx) {
        return ctx.expr().stream()
                .<QilletniType>map(t -> visitQilletniTypedNode(t, QilletniType.class))
                .toList();
    }

    @Override
    public QilletniType visitReturn_stmt(QilletniParser.Return_stmtContext ctx) {
        return visitQilletniTypedNode(ctx.expr());
    }

    @Override
    public Optional<QilletniType> visitIf_stmt(QilletniParser.If_stmtContext ctx) {
        BooleanType conditional = visitQilletniTypedNode(ctx.bool_expr());
        if (conditional.getValue()) {
            return visitNode(ctx.body());
        } else if (ctx.elseif_list() != null) { // for properly getting return val, we need to know both if this was invoked AND if it ran, right? OR, keep the result on the scope
            // result is if it went through (any if conditional was true)
            ConditionalReturning conditionalReturning = visitNode(ctx.elseif_list());
            if (conditionalReturning.finishedBranch()) {
                return conditionalReturning.returnValue();
            }
        }

        if (ctx.else_body() != null) {
            ConditionalReturning conditionalReturning = visitNode(ctx.else_body());
            return conditionalReturning.returnValue();
        }

        return Optional.empty();
    }

    @Override
    public ConditionalReturning visitElseif_list(QilletniParser.Elseif_listContext ctx) {
        if (ctx.ELSE_KEYWORD() == null) { // epsilon
            return new ConditionalReturning(false);
        }

        BooleanType conditional = visitQilletniTypedNode(ctx.bool_expr());
        if (conditional.getValue()) {
            Optional<QilletniType> returnValue = visitNode(ctx.body());
            return new ConditionalReturning(true, returnValue);
        } else if (ctx.elseif_list() != null) {
            return visitNode(ctx.elseif_list());
        }

        return new ConditionalReturning(false);
    }

    @Override
    public ConditionalReturning visitElse_body(QilletniParser.Else_bodyContext ctx) {
        if (ctx.ELSE_KEYWORD() == null) { // epsilon
            return new ConditionalReturning(false);
        }

        Optional<QilletniType> returnValue = visitNode(ctx.body());
        return new ConditionalReturning(true, returnValue);
    }

    @Override
    public Optional<QilletniType> visitFor_stmt(QilletniParser.For_stmtContext ctx) {
        var scope = symbolTable.pushScope();
        
        var range = ctx.for_expr().range();
        if (range != null) {
            var id = range.ID().getText();
            if (scope.isDefined(id)) {
                throw new AlreadyDefinedException("Symbol " + id + " has already been defined!");
            }
        }
        
        while (this.<BooleanType>visitQilletniTypedNode(ctx.for_expr()).getValue()) {
            Optional<QilletniType> bodyReturn = visitNode(ctx.body());
            if (bodyReturn.isPresent()) {
                return bodyReturn;
            }
        }
        
        symbolTable.popScope();
        
        return Optional.empty();
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
            scope.define(new Symbol<>(id, QilletniTypeClass.INT, new IntType(0)));
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
        if (ctx.ID() != null) {
            var scope = symbolTable.currentScope();
            return scope.<SongType>lookup(ctx.ID().getText()).getValue();
        }
        
        if (ctx.function_call() != null) {
            return this.<SongType>visitFunctionCallWithContext(ctx.function_call()).orElseThrow(FunctionDidntReturnException::new);
        }

        var urlOrName = ctx.song_url_or_name_pair();
        if (urlOrName.STRING().size() == 1) {
            return new SongType(StringUtility.removeQuotes(urlOrName.STRING(0).getText()));
        }

        return new SongType(StringUtility.removeQuotes(urlOrName.STRING(0).getText()), StringUtility.removeQuotes(urlOrName.STRING(1).getText()));
    }

    @Override
    public Object visitSong_url_or_name_pair(QilletniParser.Song_url_or_name_pairContext ctx) {
        throw new RuntimeException("This should never be visited!");
    }

    @Override
    public Object visitCollection_url_or_name_pair(QilletniParser.Collection_url_or_name_pairContext ctx) {
        throw new RuntimeException("This should never be visited!");
    }

    @Override
    public WeightsType visitWeights_expr(QilletniParser.Weights_exprContext ctx) {
        var scope = symbolTable.currentScope();
        
        if (ctx.ID() != null) {
            return scope.<WeightsType>lookup(ctx.ID().getText()).getValue();
        }
        
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
    
    // Entity

    @Override
    public EntityDefinition visitEntity_def(QilletniParser.Entity_defContext ctx) {
        var entityName = ctx.ID().getText();
        
        EntityAttributes attributes = visitNode(ctx.entity_body());
        var entityDefinition = new EntityDefinition(entityName, attributes.properties(), attributes.constructorParams(), attributes.entityFunctionPopulators(), globalScope);
        LOGGER.debug("Define entity: {}", entityName);
        entityDefinitionManager.defineEntity(entityDefinition);
        
        return entityDefinition;
    }

    @Override
    public EntityAttributes visitEntity_body(QilletniParser.Entity_bodyContext ctx) {
        var initializedProperties = new HashMap<String, QilletniType>();
        var unorderedUninitializedProperties = new HashMap<String, UninitializedType>();
        
        ctx.entity_property_declaration().stream().map(this::<EntityProperty<?>>visitNode).forEach(entityProperty -> {
            if (entityProperty instanceof EntityProperty<?> (var name, UninitializedType type)) {
                unorderedUninitializedProperties.put(name, type);
            } else if (entityProperty instanceof EntityProperty<?> (var name, QilletniType type)) {
                initializedProperties.put(name, type);
            }
        });
        
        List<String> params = visitNode(ctx.entity_constructor());
        if (params.size() != unorderedUninitializedProperties.size() || !params.stream().allMatch(unorderedUninitializedProperties::containsKey)) {
            throw new InvalidConstructor("Constructor parameters must match uninitialized properties of the entity");
        }
        
        // In the same order as the constructor
        var uninitializedProperties = params.stream().collect(Collectors.toMap(Function.identity(),
                unorderedUninitializedProperties::get, (o1, o2) -> o1, LinkedHashMap::new));
        
        List<Consumer<Scope>> functionPopulators = ctx.function_def()
                .stream()
                .map(functionDef -> (Consumer<Scope>) (Scope scope) -> scopedVisitFunctionDef(scope, functionDef))
                .toList();
        
        return new EntityAttributes(initializedProperties, uninitializedProperties, functionPopulators);
    }

    @Override
    public EntityProperty<?> visitEntity_property_declaration(QilletniParser.Entity_property_declarationContext ctx) {
        var text = ctx.ID(ctx.ID().size() - 1).getText();

        var type = ctx.type;
        
        if (ctx.int_expr() == null && ctx.str_expr() == null && ctx.bool_expr() == null && ctx.collection_expr() == null && ctx.song_expr() == null && ctx.weights_expr() == null && ctx.entity_initialize() == null) {
            // undefined property
            if (ctx.ID().size() == 2) { // is an Entity
                return new EntityProperty<>(text, new UninitializedType(entityDefinitionManager.lookup(ctx.ID(0).getText()))); // pass the entity name? TODO
            }
            
            return new EntityProperty<>(text, new UninitializedType(TypeUtils.getTypeFromStringOrThrow(type.getText())));
        }
        
        // is a defined property
        
        var value = switch (ctx.type.getType()) {
            case QilletniLexer.INT_TYPE -> visitQilletniTypedNode(ctx.int_expr(), IntType.class);
            case QilletniLexer.BOOLEAN_TYPE -> visitQilletniTypedNode(ctx.bool_expr(), BooleanType.class);
            case QilletniLexer.STRING_TYPE -> visitQilletniTypedNode(ctx.str_expr(), StringType.class);
            case QilletniLexer.COLLECTION_TYPE -> visitQilletniTypedNode(ctx.collection_expr(), CollectionType.class);
            case QilletniLexer.SONG_TYPE -> visitQilletniTypedNode(ctx.song_expr(), SongType.class);
            case QilletniLexer.WEIGHTS_KEYWORD -> visitQilletniTypedNode(ctx.weights_expr(), WeightsType.class);
            case QilletniLexer.ID -> null;
            default -> throw new RuntimeException("This should not be possible, unknown type");
        };
        
        return new EntityProperty<>(text, value);
    }

    @Override
    public List<String> visitEntity_constructor(QilletniParser.Entity_constructorContext ctx) {
        return visitNode(ctx.function_def_params());
    }

    @Override
    public EntityType visitEntity_initialize(QilletniParser.Entity_initializeContext ctx) {
        var entityDefinition = entityDefinitionManager.lookup(ctx.ID().getText());
        return entityDefinition.createInstance(ctx.expr_list() == null ? Collections.emptyList() : visitNode(ctx.expr_list()));
    }

    @Override
    public CollectionType visitCollection_expr(QilletniParser.Collection_exprContext ctx) {
        var scope = symbolTable.currentScope();

        if (ctx.ID() != null) {
            return scope.<CollectionType>lookup(ctx.ID().getText()).getValue();
        }

        if (ctx.function_call() != null) {
            return this.<CollectionType>visitFunctionCallWithContext(ctx.function_call()).orElseThrow(FunctionDidntReturnException::new);
        }

        var urlOrName = ctx.collection_url_or_name_pair();
        CollectionType collectionType;
        if (urlOrName.STRING().size() == 1) {
            collectionType = new CollectionType(StringUtility.removeQuotes(urlOrName.STRING(0).getText()));
        } else {
            collectionType = new CollectionType(StringUtility.removeQuotes(urlOrName.STRING(0).getText()), StringUtility.removeQuotes(urlOrName.STRING(1).getText()));
        }

        if (ctx.order_define() != null) {
            collectionType.setOrder(visitNode(ctx.order_define()));
        }
        
        if (ctx.weights_define() != null) {
            collectionType.setWeights(visitNode(ctx.weights_define()));
        }
        
        return collectionType;
    }

    @Override
    public CollectionOrder visitOrder_define(QilletniParser.Order_defineContext ctx) {
        return CollectionOrder.getFromString(ctx.COLLECTION_ORDER().getText());
    }

    @Override
    public WeightsType visitWeights_define(QilletniParser.Weights_defineContext ctx) {
        var scope = symbolTable.currentScope();

        if (ctx.ID() != null) {
            return scope.<WeightsType>lookup(ctx.ID().getText()).getValue();
        }
        
        return this.<WeightsType>visitFunctionCallWithContext(ctx.function_call()).orElseThrow(FunctionDidntReturnException::new);
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
    
    private <T extends QilletniType> ListType createQilletniList(ParseTree ctx, QilletniTypeClass<T> listType) {
        ListType list = visitNode(ctx);
        
        if (list instanceof TypelessListType) {
            return new ListType(listType, Collections.emptyList());
        }
        
        if (!listType.equals(list.getSubType())) {
            throw new TypeMismatchException("Expected list of " + listType.getTypeName() + " but received " + list.getSubType().getTypeName());
        }
        
        return list;
    }

    public <T extends QilletniType> T visitQilletniTypedNode(ParseTree ctx, Class<T> expectedType) {
        var value = this.visitNode(ctx);
        return TypeUtils.safelyCast(value, expectedType);
    }

    public <T extends QilletniType> T visitQilletniTypedNode(ParseTree ctx) {
        try {
            return this.visitNode(ctx);
        } catch (ClassCastException e) {
            LOGGER.error("Invalid types!");
            throw new TypeMismatchException("Invalid typesssss!");
        }
    }

    public <T> T visitNode(ParseTree ctx) {
        var result = ctx.accept(QilletniVisitor.this);
        if (result == null) {
            return null;
        }

        return (T) result;
    }

    // T can either be QilletniType (defined) or UninitializedType (undefined)
    public record EntityProperty<T>(String name, T type) {}

    public record ConditionalReturning(boolean finishedBranch, Optional<QilletniType> returnValue) {
        ConditionalReturning(boolean finishedBranch) {
            this(finishedBranch, Optional.empty());
        }
    }
    
}

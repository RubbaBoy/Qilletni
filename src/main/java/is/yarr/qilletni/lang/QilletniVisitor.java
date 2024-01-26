package is.yarr.qilletni.lang;

import is.yarr.qilletni.StringUtility;
import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.antlr.QilletniParserBaseVisitor;
import is.yarr.qilletni.api.music.TrackOrchestrator;
import is.yarr.qilletni.api.lang.types.weights.WeightUnit;
import is.yarr.qilletni.lang.exceptions.AlreadyDefinedException;
import is.yarr.qilletni.lang.exceptions.FunctionDidntReturnException;
import is.yarr.qilletni.lang.exceptions.FunctionInvocationException;
import is.yarr.qilletni.lang.exceptions.InvalidConstructor;
import is.yarr.qilletni.lang.exceptions.InvalidParameterException;
import is.yarr.qilletni.lang.exceptions.ListOutOfBoundsException;
import is.yarr.qilletni.lang.exceptions.QilletniException;
import is.yarr.qilletni.lang.exceptions.TypeMismatchException;
import is.yarr.qilletni.lang.exceptions.VariableNotFoundException;
import is.yarr.qilletni.lang.internal.NativeFunctionHandler;
import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.lang.table.ScopeImpl;
import is.yarr.qilletni.lang.table.SymbolImpl;
import is.yarr.qilletni.lang.table.SymbolTable;
import is.yarr.qilletni.lang.table.TableUtils;
import is.yarr.qilletni.api.lang.types.AlbumType;
import is.yarr.qilletni.lang.types.AlbumTypeImpl;
import is.yarr.qilletni.api.lang.types.BooleanType;
import is.yarr.qilletni.lang.types.BooleanTypeImpl;
import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.lang.types.CollectionTypeImpl;
import is.yarr.qilletni.lang.types.EntityTypeImpl;
import is.yarr.qilletni.lang.types.FunctionTypeImpl;
import is.yarr.qilletni.api.lang.types.IntType;
import is.yarr.qilletni.lang.types.IntTypeImpl;
import is.yarr.qilletni.api.lang.types.JavaType;
import is.yarr.qilletni.lang.types.JavaTypeImpl;
import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.lang.types.ListTypeImpl;
import is.yarr.qilletni.api.lang.types.SongType;
import is.yarr.qilletni.lang.types.SongTypeImpl;
import is.yarr.qilletni.api.lang.types.StringType;
import is.yarr.qilletni.lang.types.StringTypeImpl;
import is.yarr.qilletni.lang.types.TypeUtils;
import is.yarr.qilletni.lang.types.TypelessListType;
import is.yarr.qilletni.api.lang.types.WeightsType;
import is.yarr.qilletni.api.lang.types.collection.CollectionLimit;
import is.yarr.qilletni.api.lang.types.collection.CollectionLimitUnit;
import is.yarr.qilletni.api.lang.types.collection.CollectionOrder;
import is.yarr.qilletni.lang.types.WeightsTypeImpl;
import is.yarr.qilletni.lang.types.entity.EntityAttributes;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinition;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.lang.types.entity.EntityDefinitionImpl;
import is.yarr.qilletni.api.lang.types.entity.UninitializedType;
import is.yarr.qilletni.lang.types.entity.UninitializedTypeImpl;
import is.yarr.qilletni.lang.types.list.ListTypeTransformer;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.api.lang.types.weights.WeightEntry;
import is.yarr.qilletni.lang.types.weights.WeightEntryImpl;
import is.yarr.qilletni.api.music.MusicPopulator;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QilletniVisitor extends QilletniParserBaseVisitor<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QilletniVisitor.class);

    private final SymbolTable symbolTable;
    private final ScopeImpl globalScope;
    private final EntityDefinitionManager entityDefinitionManager;
    private final NativeFunctionHandler nativeFunctionHandler;
    private final MusicPopulator musicPopulator;
    private final ListTypeTransformer listTypeTransformer;
    private final Consumer<String> importConsumer;
    private final TrackOrchestrator trackOrchestrator;

    public QilletniVisitor(SymbolTable symbolTable, ScopeImpl globalScope, EntityDefinitionManager entityDefinitionManager, NativeFunctionHandler nativeFunctionHandler, MusicPopulator musicPopulator, ListTypeTransformer listTypeTransformer, TrackOrchestrator trackOrchestrator, Consumer<String> importConsumer) {
        this.symbolTable = symbolTable;
        this.globalScope = globalScope;
        this.entityDefinitionManager = entityDefinitionManager;
        this.nativeFunctionHandler = nativeFunctionHandler;
        this.musicPopulator = musicPopulator;
        this.listTypeTransformer = listTypeTransformer;
        this.importConsumer = importConsumer;
        this.trackOrchestrator = trackOrchestrator;
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
        QilletniTypeClass<?> onType = null;
        if (ctx.function_on_type() != null) {
            onType = visitNode(ctx.function_on_type());
        }

        scopedDefineFunction(scope, ctx, onType, onType, true);
    }

    private void scopedVisitEntityFunctionDef(Scope scope, QilletniParser.Function_defContext ctx, QilletniTypeClass<?> onType) {
        scopedDefineFunction(scope, ctx, onType, onType, false);
    }

    private void scopedDefineFunction(Scope scope, QilletniParser.Function_defContext ctx, QilletniTypeClass<?> implOnType, QilletniTypeClass<?> nativeOnType, boolean isExternallyDefined) {
        var id = ctx.ID().getText();

        LOGGER.debug("Defining func of {}", id);

        var params = new ArrayList<String>(visitNode(ctx.function_def_params()));

        int definedParamCount = params.size();

        if (ctx.NATIVE() != null) {
            int invokingParamCount = params.size();
            
            if (implOnType != null) {
                definedParamCount++;
            }
            
            // If it is native, force it to have the on type of the entity
            scope.defineFunction(SymbolImpl.createFunctionSymbol(id, FunctionTypeImpl.createNativeFunction(id, params.toArray(String[]::new), invokingParamCount, definedParamCount, isExternallyDefined, nativeOnType)));
        } else {
            int invokingParamCount = params.size();
            if (implOnType != null && isExternallyDefined) {
                invokingParamCount--;
            }
            
            LOGGER.debug("{} params = {} (defined: {}) on type = {}", id, params, definedParamCount, implOnType);
            scope.defineFunction(SymbolImpl.createFunctionSymbol(id, FunctionTypeImpl.createImplementedFunction(id, params.toArray(String[]::new), invokingParamCount, definedParamCount, isExternallyDefined, implOnType, ctx.body())));
        }
    }

    @Override
    public List<String> visitFunction_def_params(QilletniParser.Function_def_paramsContext ctx) {
        return ctx.ID().stream().map(ParseTree::getText).toList();
    }

    @Override
    public QilletniTypeClass<?> visitFunction_on_type(QilletniParser.Function_on_typeContext ctx) {
        return TypeUtils.getTypeFromStringOrEntity(ctx.type.getText());
    }

    @Override
    public Object visitAsmt(QilletniParser.AsmtContext ctx) {
        var id = ctx.ID(ctx.ID().size() - 1).getText();

        var currentScope = symbolTable.currentScope();

        if (ctx.type != null && ctx.LEFT_SBRACKET() != null) { // defining a new list
            var expr = ctx.expr(0);
            ListType assignmentValue = switch (ctx.type.getType()) {
                case QilletniLexer.INT_TYPE -> createListOfTypeFromExpression(expr, QilletniTypeClass.INT);
                case QilletniLexer.BOOLEAN_TYPE -> createListOfTypeFromExpression(expr, QilletniTypeClass.BOOLEAN);
                case QilletniLexer.STRING_TYPE -> createListOfTypeFromExpression(expr, QilletniTypeClass.STRING);
                case QilletniLexer.COLLECTION_TYPE -> createListOfTypeFromExpression(expr, QilletniTypeClass.COLLECTION);
                case QilletniLexer.SONG_TYPE -> createListOfTypeFromExpression(expr, QilletniTypeClass.SONG);
                case QilletniLexer.ALBUM_TYPE -> createListOfTypeFromExpression(expr, QilletniTypeClass.ALBUM);
                case QilletniLexer.WEIGHTS_KEYWORD -> createListOfTypeFromExpression(expr, QilletniTypeClass.WEIGHTS);
                case QilletniLexer.ID -> {
                    var entityName = ctx.type.getText();

                    var expectedEntity = entityDefinitionManager.lookup(entityName).getQilletniTypeClass();
                    var entityNode = createListOfTypeFromExpression(expr, QilletniTypeClass.createEntityTypePlaceholder(entityName));

                    var listSubtypeClass = entityNode.getSubType();
                    if (!listSubtypeClass.equals(expectedEntity)) {
                        var gotTypeName = listSubtypeClass.getEntityDefinition().getTypeName();
                        throw new TypeMismatchException(ctx, "Expected entity " + entityName + ", got " + gotTypeName);
                    }

                    yield entityNode;
                }
                default -> throw new RuntimeException("This should not be possible, unknown type");
            };

            LOGGER.debug("(new) {}[] = {}", id, assignmentValue);
            currentScope.define(SymbolImpl.createGenericSymbol(id, assignmentValue.getTypeClass(), assignmentValue));

            // visitQilletniList
        } else if (ctx.LEFT_SBRACKET() != null) { // foo[123] = expr
            var listSymbol = currentScope.<ListTypeImpl>lookup(id);
            var list = listSymbol.getValue();
            var index = visitQilletniTypedNode(ctx.int_expr(), IntTypeImpl.class).getValue();

            if (index < 0 || index > list.getItems().size()) {
                throw new ListOutOfBoundsException(ctx, "Attempted to access index " + index + " on a list with a size of " + list.getItems().size());
            }

            var expressionValue = visitQilletniTypedNode(ctx.expr(0));
            if (!expressionValue.getTypeClass().equals(list.getSubType())) {
                throw new TypeMismatchException(ctx, "Attempted to assign a " + expressionValue.typeName() + " in a " + list.typeName() + " list");
            }

            var mutableItems = new ArrayList<>(list.getItems());
            mutableItems.set(index, expressionValue);
            list.setItems(mutableItems);

            listSymbol.setValue(list); // Not really needed
        } else if (ctx.type != null) { // defining a new var
            var expr = ctx.expr(0);
            QilletniType assignmentValue = switch (ctx.type.getType()) {
                case QilletniLexer.INT_TYPE -> visitQilletniTypedNode(expr, IntTypeImpl.class);
                case QilletniLexer.BOOLEAN_TYPE -> visitQilletniTypedNode(expr, BooleanTypeImpl.class);
                case QilletniLexer.STRING_TYPE -> visitQilletniTypedNode(expr, StringTypeImpl.class);
                case QilletniLexer.COLLECTION_TYPE -> {
                    var value = visitQilletniTypedNode(expr);
                    if (!(value instanceof StringType stringType)) {
                        yield TypeUtils.safelyCast(value, CollectionTypeImpl.class);
                    }

                    yield musicPopulator.initiallyPopulateCollection(new CollectionTypeImpl(stringType.stringValue()));
                }
                case QilletniLexer.SONG_TYPE -> {
                    var value = visitQilletniTypedNode(expr);
                    if (!(value instanceof StringType stringType)) {
                        yield TypeUtils.safelyCast(value, SongTypeImpl.class);
                    }

                    yield musicPopulator.initiallyPopulateSong(new SongTypeImpl(stringType.stringValue()));
                }
                case QilletniLexer.ALBUM_TYPE -> {
                    var value = visitQilletniTypedNode(expr);
                    if (!(value instanceof StringType stringType)) {
                        yield TypeUtils.safelyCast(value, AlbumTypeImpl.class);
                    }
                    
                    yield musicPopulator.initiallyPopulateAlbum(new AlbumTypeImpl(stringType.stringValue()));
                }
                case QilletniLexer.WEIGHTS_KEYWORD -> visitQilletniTypedNode(expr, WeightsTypeImpl.class);
                case QilletniLexer.ID -> {
                    var entityName = ctx.type.getText();

                    var expectedEntity = entityDefinitionManager.lookup(entityName);
                    var entityNode = visitQilletniTypedNode(expr, EntityTypeImpl.class);

                    var gotTypeName = entityNode.getEntityDefinition().getTypeName();
                    if (!entityNode.getEntityDefinition().equals(expectedEntity)) {
                        throw new TypeMismatchException(ctx, "Expected entity " + entityName + ", got " + gotTypeName);
                    }

                    yield entityNode;
                }
                default -> throw new RuntimeException("This should not be possible, unknown type");
            };

            LOGGER.debug("(new) {} = {}", id, assignmentValue);
            currentScope.define(SymbolImpl.createGenericSymbol(id, assignmentValue.getTypeClass(), assignmentValue));
        } else if (ctx.expr_assign != null) { // foo.bar = baz

            if (id.startsWith("_")) {
                throw new VariableNotFoundException(ctx.expr_assign, "Cannot access private variable");
            }
            
            var entity = visitQilletniTypedNode(ctx.expr(0), EntityTypeImpl.class);
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
        if (ctx.ID() != null) {
            var idText = ctx.ID().getText();

            if (ctx.LEFT_SBRACKET() != null) { // foo[123]
                var list = currentScope.<ListTypeImpl>lookup(idText).getValue().getItems();
                var index = visitQilletniTypedNode(ctx.int_expr(), IntTypeImpl.class).getValue();

                if (index < 0 || index > list.size()) {
                    throw new ListOutOfBoundsException(ctx, "Attempted to access index " + index + " on a list with a size of " + list.size());
                }

                return list.get(index);
            }

            if (ctx.DOT() == null) { // id
                LOGGER.debug("Visiting expr! ID: {}", ctx.ID());
                return currentScope.lookup(idText).getValue();
            } else { // foo.baz
                if (idText.startsWith("_")) {
                    throw new VariableNotFoundException(ctx, "Cannot access private variable");
                }
                
                var entity = visitQilletniTypedNode(ctx.expr(0), EntityTypeImpl.class);
                LOGGER.debug("Getting property {} on entity {}", idText, entity.typeName());
                var entityScope = entity.getEntityScope();
                return entityScope.lookup(idText).getValue();
            }
        }
        
        if (ctx.REL_OP() != null) {
            var leftChild = ctx.expr(0);
            var rightChild = ctx.expr(1);
            var leftType = visitQilletniTypedNode(leftChild);
            var rightType = visitQilletniTypedNode(rightChild);

            var relOpVal = ctx.REL_OP().getSymbol().getText();

            if ("!==".contains(relOpVal)) {
                var areEqual = leftType.qilletniEquals(rightType);
                
                if (relOpVal.equals("!=")) {
                    areEqual = !areEqual;
                }
                
                return new BooleanTypeImpl(areEqual);
            } else {
                var leftInt = TypeUtils.safelyCast(leftType, IntTypeImpl.class).getValue();
                var rightInt = TypeUtils.safelyCast(rightType, IntTypeImpl.class).getValue();

                LOGGER.debug("Comparing {} {} {}", leftInt, relOpVal, rightInt);

                var comparisonResult = switch (relOpVal) {
                    case ">" -> leftInt > rightInt;
                    case "<" -> leftInt < rightInt;
                    case "<=" -> leftInt <= rightInt;
                    case ">=" -> leftInt >= rightInt;
                    default -> throw new IllegalStateException("Unexpected value: " + relOpVal);
                };

                return new BooleanTypeImpl(comparisonResult);
            }
        }

        if (ctx.DOT() != null) { // foo.bar()
            if (ctx.function_call().ID().getText().startsWith("_")) {
                throw new FunctionInvocationException(ctx, "Cannot invoke private function");
            }
            
            var leftExpr = visitQilletniTypedNode(ctx.expr(0));
            return visitFunctionCallWithContext(ctx.function_call(), leftExpr).orElseThrow(FunctionDidntReturnException::new);
        }

        if (ctx.LEFT_PAREN() != null) { // ( expr )
            return visitQilletniTypedNode(ctx.getChild(1));
        }

        if (ctx.PLUS() != null) { // id + id
            var leftExpr = visitQilletniTypedNode(ctx.expr(0));
            var rightExpr = visitQilletniTypedNode(ctx.expr(1));

            if (leftExpr instanceof IntType leftInt && rightExpr instanceof IntType rightInt) {
                var leftValue = leftInt.getValue();
                var rightValue = rightInt.getValue();
                
                return new IntTypeImpl(leftValue + rightValue);
            }

            return new StringTypeImpl(leftExpr.stringValue() + rightExpr.stringValue());
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
                value = StringTypeImpl.fromType(symbolTable.currentScope().lookup(symbol.getText()).getValue());
            } else if (type == QilletniLexer.STRING) {
                var stringLiteral = symbol.getText();
                value = new StringTypeImpl(stringLiteral.substring(1, stringLiteral.length() - 1).translateEscapes());
            }
        } else if (child instanceof QilletniParser.Function_callContext functionCallContext) {
            value = this.<StringTypeImpl>visitFunctionCallWithContext(functionCallContext).orElseThrow(FunctionDidntReturnException::new);
        } else if (child instanceof QilletniParser.Str_exprContext) {
            value = visitQilletniTypedNode(child);
        } else if (child instanceof QilletniParser.ExprContext) {
            value = new StringTypeImpl(String.valueOf(visitQilletniTypedNode(child)));
        }

        if (ctx.getChildCount() == 3) { // ( str_expr )  or  str_expr + str_expr 
            var middle = ctx.getChild(1);
            if (middle instanceof TerminalNode term && term.getSymbol().getType() == QilletniLexer.PLUS) {
                if (value == null) {
                    value = new StringTypeImpl("null");
                }

                var add = visitQilletniTypedNode(ctx.getChild(2));
                value = new StringTypeImpl(value.getValue() + add.stringValue());
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
                value = symbolTable.currentScope().<IntTypeImpl>lookup(symbol.getText()).getValue();
            } else if (type == QilletniLexer.INT) {
                value = new IntTypeImpl(Integer.parseInt(symbol.getText()));
            }
        } else if (child instanceof QilletniParser.Function_callContext functionCallContext) {
            value = this.<IntTypeImpl>visitFunctionCallWithContext(functionCallContext).orElseThrow(FunctionDidntReturnException::new);
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
            value = new IntTypeImpl(calculate.apply(intVal, add.getValue()));
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
                value = symbolTable.currentScope().<BooleanTypeImpl>lookup(symbol.getText()).getValue();
            } else if (type == QilletniLexer.BOOL) {
                value = new BooleanTypeImpl(symbol.getText().equals("true"));
            }
        } else if (child instanceof QilletniParser.Function_callContext functionCallContext) {
            value = this.<BooleanTypeImpl>visitFunctionCallWithContext(functionCallContext).orElseThrow(FunctionDidntReturnException::new);
            ;
        } 
//        else if (ctx.REL_OP() != null && ctx.getChild(1) instanceof TerminalNode relOp) {
//            var leftChild = ctx.getChild(0);
//            var rightChild = ctx.getChild(2);
//            System.out.println("leftChild = " + leftChild.getClass());
//            System.out.println("leftChild = " + leftChild.getText());
//            var leftType = visitQilletniTypedNode(leftChild);
//            var rightType = visitQilletniTypedNode(rightChild);
//
//            var relOpVal = relOp.getSymbol().getText();
//
//            if ("!==".contains(relOpVal)) {
//                BiFunction<Comparable<?>, Comparable<?>, Boolean> compareMethod = relOpVal.equals("==") ?
//                        Objects::equals : (a, b) -> !Objects.equals(a, b);
//
//                if (leftChild instanceof QilletniParser.Int_exprContext && rightChild instanceof QilletniParser.Int_exprContext) {
//                    var leftInt = TypeUtils.safelyCast(leftType, IntType.class).getValue();
//                    var rightInt = TypeUtils.safelyCast(rightType, IntType.class).getValue();
//
//                    return new BooleanType(compareMethod.apply(leftInt, rightInt));
//                } else if (leftChild instanceof QilletniParser.Bool_exprContext && rightChild instanceof QilletniParser.Bool_exprContext) {
//                    var leftBool = TypeUtils.safelyCast(leftType, BooleanType.class).getValue();
//                    var rightBool = TypeUtils.safelyCast(rightType, BooleanType.class).getValue();
//
//                    return new BooleanType(compareMethod.apply(leftBool, rightBool));
//                }
//
//                throw new TypeMismatchException(ctx, "Cannot compare differing types");
//            } else {
//                var leftInt = TypeUtils.safelyCast(leftType, IntType.class).getValue();
//                var rightInt = TypeUtils.safelyCast(rightType, IntType.class).getValue();
//
//                LOGGER.debug("Comparing {} {} {}", leftInt, relOpVal, rightInt);
//
//                var comparisonResult = switch (relOpVal) {
//                    case ">" -> leftInt > rightInt;
//                    case "<" -> leftInt < rightInt;
//                    case "<=" -> leftInt <= rightInt;
//                    case ">=" -> leftInt >= rightInt;
//                    default -> throw new IllegalStateException("Unexpected value: " + relOpVal);
//                };
//
//                return new BooleanType(comparisonResult);
//            }
//        }

        return value;
    }

    @Override
    public ListType visitList_expression(QilletniParser.List_expressionContext ctx) {
        return createListOfAnyType(ctx);
    }

    /**
     * Checks if the {@link ListTypeImpl} has already been computed, or empty. If so, return the list. If not, return an
     * empty optional.
     * 
     * @param ctx The list expression
     * @return The already computed list, if it exists
     */
    private Optional<ListType> checkAlreadyComputedList(QilletniParser.List_expressionContext ctx) {
        if (ctx.type == null && ctx.ID() != null) {
            var scope = symbolTable.currentScope();
            return Optional.of(scope.<ListTypeImpl>lookup(ctx.ID().getText()).getValue());
        }

        if (ctx.expr_list() == null) {
            return Optional.of(new TypelessListType());
        }
        
        return Optional.empty();
    }
    
    private ListType createListOfAnyType(QilletniParser.List_expressionContext ctx) {
        if (ctx.type != null) {
            var listType = TypeUtils.getTypeFromStringOrEntity(ctx.type.getText());
            return createListOfType(ctx, listType);
        }
        
        return checkAlreadyComputedList(ctx).orElseGet(() -> {
            var items = this.<List<QilletniType>>visitNode(ctx.expr_list());

            var typeList = items.stream().map(QilletniType::getTypeClass).distinct().toList();
            if (typeList.size() > 1) {
                throw new TypeMismatchException(ctx, "Multiple types found in list");
            }

            return new ListTypeImpl(typeList.get(0), items);
        });
    }
    
    private <T extends QilletniType> ListType createListOfType(QilletniParser.List_expressionContext ctx, QilletniTypeClass<T> listType) {
        return checkAlreadyComputedList(ctx).orElseGet(() -> {
            var items = this.<List<QilletniType>>visitNode(ctx.expr_list());

            var transformedItems = items.stream().map(listItem -> {
                if (listItem.getTypeClass().equals(listType)) {
                    return listItem;
                }

                return listTypeTransformer.transformType(listType, listItem);
            }).toList();

            return new ListTypeImpl(listType, transformedItems);
        });
    }

    /**
     * Creates a {@link ListTypeImpl} of the type {@code listType} from an {@link ParserRuleContext} that is assumed to have
     * one child of {@link is.yarr.qilletni.antlr.QilletniParser.List_expressionContext}.
     *
     * @param exprCtx           The expression context that is assumed to be a list
     * @param listType          The type the list should be in the end
     * @return The created list
     * @param <T> The type of the list items should be converted to
     */
    private <T extends QilletniType> ListType createListOfTypeFromExpression(ParserRuleContext exprCtx, QilletniTypeClass<T> listType) {
        var ctx = exprCtx.getChild(QilletniParser.List_expressionContext.class, 0);
        
        if (ctx == null) {
            throw new TypeMismatchException(exprCtx, "Expected list expression");
        }
        
        var list = createListOfType(ctx, listType);

        if (list instanceof TypelessListType) {
            return new ListTypeImpl(listType, Collections.emptyList());
        }

        return list;
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
//        var swappedScope = false;
        var hasOnType = invokedOn != null;
        
        
//        LOGGER.debug("swap scope = ({} != null) && {}", invokedOn, invokedOn instanceof EntityType);
        var swappedLookupScope = false;
        // swap lookup scope
        LOGGER.debug("({}) invokedOn = {}, and {}", id, invokedOn, invokedOn instanceof EntityType);
        if (invokedOn instanceof EntityType entityType) {
            LOGGER.debug("SWAP scope! to {}", entityType.getEntityScope().getAllSymbols().keySet());
//            swappedScope = true;
//            hasOnType = false;
            swappedLookupScope = true;
            symbolTable.swapScope(entityType.getEntityScope());
        }
        
        var scope = symbolTable.currentScope();

        List<QilletniType> params = new ArrayList<>();
        if (ctx.expr_list() != null) {
            params.addAll(visitNode(ctx.expr_list()));
        }

        var functionType = scope.lookupFunction(id, params.size(), invokedOn != null ? invokedOn.getTypeClass() : null).getValue();

        if (hasOnType && !functionType.getOnType().equals(invokedOn.getTypeClass())) {
            throw new FunctionInvocationException(ctx, "Function not to be invoked on " + invokedOn.getTypeClass() + " should be " + functionType.getOnType());
        }

        var swapInvocationScope = false;
        
        // If this has an on type and is native, we need to allow it to look it up in the native function handler with the on type
        // This isn't needed if it's implemented, as it has no additional type param
        if (swappedLookupScope) {
            if (functionType.isNative() || functionType.isExternallyDefined()) {
                // Return to normal scope if either native (wouldn't really make a difference but might as well) or
                // if it is externally defined, so it is invoked normally.
                symbolTable.unswapScope();
            } else {
                swapInvocationScope = true;
            }
        }
        
        LOGGER.debug("swapInvocatioonScope = !({} || {})", functionType.isNative(), functionType.isExternallyDefined());
        
        var functionParams = new ArrayList<>(Arrays.asList(functionType.getParams()));

        var expectedParamLength = functionType.getInvokingParamCount();
        
        if (expectedParamLength != params.size()) {
            throw new InvalidParameterException(ctx, "Expected " + expectedParamLength + " parameters, got " + params.size() + " onType: " + hasOnType);
        }

        QilletniTypeClass<?> invokingUponExpressionType = null;
        // If there is an on type param, add it
        LOGGER.debug("{} != {}", functionType.getInvokingParamCount(), functionType.getDefinedParamCount());
        LOGGER.debug("func: {}", functionType.getName());
        if (invokedOn != null) {
            invokingUponExpressionType = invokedOn.getTypeClass();
            
            if (functionType.getInvokingParamCount() != functionType.getDefinedParamCount()) {
                params.add(0, invokedOn);
            }
        }

        if (functionType.isNative()) {
            LOGGER.debug("Invoking native! {}", functionType.getName());
            return Optional.ofNullable((T) nativeFunctionHandler.invokeNativeMethod(ctx, functionType.getName(), params, functionType.getDefinedParamCount(), invokingUponExpressionType));
        }

        LOGGER.debug("1 curr scope = {}", symbolTable.currentScope());
        var functionScope = symbolTable.functionCall();

        LOGGER.debug("2 curr scope = {}", symbolTable.currentScope());

        for (int i = 0; i < params.size(); i++) {
            var qilletniType = params.get(i);
            functionScope.define(SymbolImpl.createGenericSymbol(functionParams.get(i), qilletniType.getTypeClass(), qilletniType));
        }

        Optional<T> result = visitNode(functionType.getBody());

        symbolTable.endFunctionCall();

        LOGGER.debug("3 curr scope = {}", symbolTable.currentScope());

        if (swapInvocationScope) {
            LOGGER.debug("UNSWAP scope!");
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
        BooleanType conditional = visitQilletniTypedNode(ctx.expr());
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

        BooleanType conditional = visitQilletniTypedNode(ctx.expr());
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

        for (int i = 0; visitForExpression(ctx.for_expr(), i).getValue(); i++) {
            Optional<QilletniType> bodyReturn = visitNode(ctx.body());
            if (bodyReturn.isPresent()) {
                return bodyReturn;
            }
        }

        symbolTable.popScope();

        return Optional.empty();
    }

    /**
     * 
     * @param ctx
     * @param index
     * @return If the for loop should iterate
     */
    public BooleanType visitForExpression(QilletniParser.For_exprContext ctx, int index) {
        if (ctx.bool_expr() != null) {
            return visitQilletniTypedNode(ctx.bool_expr());
        } else if (ctx.range() != null) {
            return visitQilletniTypedNode(ctx.range());
        } else if (ctx.foreach_range() != null) {
            return visitForeachRange(ctx.foreach_range(), index);
        }

        // Should never happen
        return BooleanTypeImpl.FALSE;
    }

    @Override
    public BooleanType visitFor_expr(QilletniParser.For_exprContext ctx) {
        throw new RuntimeException();
    }

    @Override
    public BooleanType visitRange(QilletniParser.RangeContext ctx) {
        var scope = symbolTable.currentScope();
        var id = ctx.ID().getText();

        var rangeTo = ctx.RANGE_INFINITY() != null ? Integer.MAX_VALUE : Integer.parseInt(ctx.getChild(2).getText());

        if (!scope.isDefined(id)) { // first iteration, let it pass
            scope.define(new SymbolImpl<>(id, QilletniTypeClass.INT, new IntTypeImpl(0)));
            return BooleanTypeImpl.TRUE;
        }

        var idIntType = scope.<IntTypeImpl>lookup(id);
        var newValue = idIntType.getValue().getValue() + 1;
        idIntType.setValue(new IntTypeImpl(newValue));

        if (rangeTo > newValue) {
            return BooleanTypeImpl.TRUE;
        }

        return BooleanTypeImpl.FALSE;
    }

    public BooleanType visitForeachRange(QilletniParser.Foreach_rangeContext ctx, int index) {
        var scope = symbolTable.currentScope();
        var variableName = ctx.ID().getText();
        var list = visitQilletniTypedNode(ctx.expr(), ListTypeImpl.class);
        
        if (index >= list.getItems().size()) {
            return BooleanTypeImpl.FALSE;
        }

        var currentItem = list.getItems().get(index);

        if (!scope.isDefined(variableName)) { // first iteration, let it pass
            scope.define(SymbolImpl.createGenericSymbol(variableName, list.getSubType(), currentItem));
            return BooleanTypeImpl.TRUE;
        }
        
        var itemType = scope.lookup(variableName);
        itemType.setValue(currentItem);
        
        return BooleanTypeImpl.TRUE;
    }
    
    @Override
    public BooleanType visitForeach_range(QilletniParser.Foreach_rangeContext ctx) {
        throw new RuntimeException();
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
            return scope.<SongTypeImpl>lookup(ctx.ID().getText()).getValue();
        }

        if (ctx.function_call() != null) {
            return this.<SongTypeImpl>visitFunctionCallWithContext(ctx.function_call()).orElseThrow(FunctionDidntReturnException::new);
        }

        var urlOrName = ctx.song_url_or_name_pair();
        SongType songType;
        if (ctx.STRING() != null) {
            songType = new SongTypeImpl(ctx.STRING().getText());
        } else {
            songType = new SongTypeImpl(StringUtility.removeQuotes(urlOrName.STRING(0).getText()), StringUtility.removeQuotes(urlOrName.STRING(1).getText()));
        }

        return musicPopulator.initiallyPopulateSong(songType);
    }

    @Override
    public AlbumType visitAlbum_expr(QilletniParser.Album_exprContext ctx) {
        if (ctx.ID() != null) {
            var scope = symbolTable.currentScope();
            return scope.<AlbumTypeImpl>lookup(ctx.ID().getText()).getValue();
        }

        if (ctx.function_call() != null) {
            return this.<AlbumTypeImpl>visitFunctionCallWithContext(ctx.function_call()).orElseThrow(FunctionDidntReturnException::new);
        }

        var urlOrName = ctx.album_url_or_name_pair();
        
        AlbumType albumType;
        if (ctx.STRING() != null) {
            albumType = new AlbumTypeImpl(StringUtility.removeQuotes(ctx.STRING().getText()));
        } else {
            albumType = new AlbumTypeImpl(StringUtility.removeQuotes(urlOrName.STRING(0).getText()), StringUtility.removeQuotes(urlOrName.STRING(1).getText()));
        }

        return musicPopulator.initiallyPopulateAlbum(albumType);
    }

    @Override
    public Object visitSong_url_or_name_pair(QilletniParser.Song_url_or_name_pairContext ctx) {
        throw new RuntimeException("This should never be visited!");
    }

    @Override
    public Object visitAlbum_url_or_name_pair(QilletniParser.Album_url_or_name_pairContext ctx) {
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
            return scope.<WeightsTypeImpl>lookup(ctx.ID().getText()).getValue();
        }

        var weights = ctx.single_weight().stream().map(this::<WeightEntry>visitNode).toList();
        return new WeightsTypeImpl(weights);
    }

    @Override
    public WeightEntry visitSingle_weight(QilletniParser.Single_weightContext ctx) {
        var weightAmount = ctx.weight_amount();
        var weightInt = Integer.parseInt(weightAmount.INT().getText());
        SongType song = visitQilletniTypedNode(ctx.song_expr());
        var canRepeat = ctx.WEIGHT_PIPE().getText().equals("|!");

        return new WeightEntryImpl(weightInt, WeightUnit.fromSymbol(weightAmount.WEIGHT_UNIT().getText()), song, canRepeat);
    }

    @Override
    public Object visitPlay_stmt(QilletniParser.Play_stmtContext ctx) {
        if (ctx.song_expr() != null) {
            var song = visitQilletniTypedNode(ctx.song_expr(), SongType.class);
            musicPopulator.populateSong(song);
            trackOrchestrator.playTrack(song.getTrack());
            return null;
        }

        var collection = visitQilletniTypedNode(ctx.collection_expr(), CollectionType.class);

        musicPopulator.populateCollection(collection);
        if (ctx.collection_limit() != null) {
            CollectionLimit limit = visitNode(ctx.collection_limit());
            LOGGER.debug("Playing collection {} with a limit of {}", collection, limit);
            trackOrchestrator.playCollection(collection, limit);
        } else {
            LOGGER.debug("Playing collection {}", collection);
            trackOrchestrator.playCollection(collection, ctx.LOOP_PARAM() != null);
        }

        return null;
    }

    @Override
    public JavaType visitJava_expr(QilletniParser.Java_exprContext ctx) {
        if (ctx.ID() != null) {
            var scope = symbolTable.currentScope();
            return scope.<JavaTypeImpl>lookup(ctx.ID().getText()).getValue();
        }

        if (ctx.function_call() != null) {
            return this.<JavaTypeImpl>visitFunctionCallWithContext(ctx.function_call()).orElseThrow(FunctionDidntReturnException::new);
        }
        
        // Has to be empty
        return new JavaTypeImpl(null);
    }

    // Entity

    @Override
    public EntityDefinition visitEntity_def(QilletniParser.Entity_defContext ctx) {
        var entityName = ctx.ID().getText();
        
        var scope = symbolTable.currentScope();
        
        EntityAttributes attributes = createEntityBody(ctx.entity_body(), entityName);
        var entityDefinition = new EntityDefinitionImpl(entityName, attributes.properties(), attributes.constructorParams(), attributes.entityFunctionPopulators(), scope);
        LOGGER.debug("Define entity: {}", entityName);
        entityDefinitionManager.defineEntity(entityDefinition);

        return entityDefinition;
    }

    private EntityAttributes createEntityBody(QilletniParser.Entity_bodyContext ctx, String entityName) {
        var initializedProperties = new HashMap<String, QilletniType>();
        var unorderedUninitializedProperties = new HashMap<String, UninitializedType>();

        ctx.entity_property_declaration().stream().map(this::<EntityProperty<?>>visitNode).forEach(entityProperty -> {
            if (entityProperty instanceof EntityProperty<?>(var name, UninitializedType type)) {
                unorderedUninitializedProperties.put(name, type);
            } else if (entityProperty instanceof EntityProperty<?>(var name, QilletniType type)) {
                initializedProperties.put(name, type);
            }
        });

        List<String> params = ctx.entity_constructor() == null ? Collections.emptyList() : visitNode(ctx.entity_constructor());
        if (params.size() != unorderedUninitializedProperties.size() || !params.stream().allMatch(unorderedUninitializedProperties::containsKey)) {
            throw new InvalidConstructor(ctx, "Constructor parameters must match uninitialized properties of the entity");
        }

        // In the same order as the constructor
        var uninitializedProperties = params.stream().collect(Collectors.toMap(Function.identity(),
                unorderedUninitializedProperties::get, (o1, o2) -> o1, LinkedHashMap::new));

        // We don't have the actual QilletniTypeClass for the entity yet, so use a placeholder type
        var onType = QilletniTypeClass.createEntityTypePlaceholder(entityName);

        List<Consumer<Scope>> functionPopulators = ctx.function_def()
                .stream()
                .map(functionDef -> (Consumer<Scope>) (Scope scope) -> scopedVisitEntityFunctionDef(scope, functionDef, onType))
                .toList();

        return new EntityAttributes(initializedProperties, uninitializedProperties, functionPopulators);
    }

    @Override
    public EntityAttributes visitEntity_body(QilletniParser.Entity_bodyContext ctx) {
        throw new RuntimeException("This should never be invoked directly");
    }

    @Override
    public EntityProperty<?> visitEntity_property_declaration(QilletniParser.Entity_property_declarationContext ctx) {
        var text = ctx.ID(ctx.ID().size() - 1).getText();

        var type = ctx.type;

        if (ctx.int_expr() == null && ctx.str_expr() == null && ctx.bool_expr() == null && ctx.collection_expr() == null && ctx.song_expr() == null && ctx.weights_expr() == null && ctx.entity_initialize() == null && ctx.java_expr() == null) {
            // undefined property
            if (ctx.ID().size() == 2) { // is an Entity
                return new EntityProperty<>(text, new UninitializedTypeImpl(entityDefinitionManager.lookup(ctx.ID(0).getText()))); // pass the entity name? TODO
            }

            return new EntityProperty<>(text, new UninitializedTypeImpl(TypeUtils.getTypeFromStringOrThrow(type.getText())));
        }

        // is a defined property

        var value = switch (ctx.type.getType()) {
            case QilletniLexer.INT_TYPE -> visitQilletniTypedNode(ctx.int_expr(), IntTypeImpl.class);
            case QilletniLexer.BOOLEAN_TYPE -> visitQilletniTypedNode(ctx.bool_expr(), BooleanTypeImpl.class);
            case QilletniLexer.STRING_TYPE -> visitQilletniTypedNode(ctx.str_expr(), StringTypeImpl.class);
            case QilletniLexer.COLLECTION_TYPE -> visitQilletniTypedNode(ctx.collection_expr(), CollectionTypeImpl.class);
            case QilletniLexer.SONG_TYPE -> visitQilletniTypedNode(ctx.song_expr(), SongTypeImpl.class);
            case QilletniLexer.WEIGHTS_KEYWORD -> visitQilletniTypedNode(ctx.weights_expr(), WeightsTypeImpl.class);
            case QilletniLexer.ALBUM_TYPE -> visitQilletniTypedNode(ctx.album_expr(), AlbumTypeImpl.class);
            case QilletniLexer.JAVA_TYPE -> visitQilletniTypedNode(ctx.java_expr(), JavaTypeImpl.class);
            case QilletniLexer.ID -> visitQilletniTypedNode(ctx.entity_initialize(), EntityTypeImpl.class);
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
            return scope.<CollectionTypeImpl>lookup(ctx.ID().getText()).getValue();
        }

        if (ctx.function_call() != null) {
            return this.<CollectionTypeImpl>visitFunctionCallWithContext(ctx.function_call()).orElseThrow(FunctionDidntReturnException::new);
        }

        var urlOrName = ctx.collection_url_or_name_pair();
        CollectionType collectionType;
        if (ctx.STRING() != null) {
            collectionType = new CollectionTypeImpl(StringUtility.removeQuotes(ctx.STRING().getText()));
        } else {
            collectionType = new CollectionTypeImpl(StringUtility.removeQuotes(urlOrName.STRING(0).getText()), StringUtility.removeQuotes(urlOrName.STRING(1).getText()));
        }

        if (ctx.order_define() != null) {
            collectionType.setOrder(visitNode(ctx.order_define()));
        }

        if (ctx.weights_define() != null) {
            collectionType.setWeights(visitNode(ctx.weights_define()));
        }

        return musicPopulator.initiallyPopulateCollection(collectionType);
    }

    @Override
    public CollectionOrder visitOrder_define(QilletniParser.Order_defineContext ctx) {
        return CollectionOrder.getFromString(ctx.COLLECTION_ORDER().getText());
    }

    @Override
    public WeightsType visitWeights_define(QilletniParser.Weights_defineContext ctx) {
        var scope = symbolTable.currentScope();

        if (ctx.ID() != null) {
            return scope.<WeightsTypeImpl>lookup(ctx.ID().getText()).getValue();
        }

        return this.<WeightsTypeImpl>visitFunctionCallWithContext(ctx.function_call()).orElseThrow(FunctionDidntReturnException::new);
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

    public <T extends QilletniType> T visitQilletniTypedNode(ParseTree ctx, Class<T> expectedType) {
        var value = this.visitNode(ctx);
        return TypeUtils.safelyCast(value, expectedType);
    }

    public <T extends QilletniType> T visitQilletniTypedNode(ParserRuleContext ctx) {
        return visitQilletniTypedNode((ParseTree) ctx);
    }

    public <T extends QilletniType> T visitQilletniTypedNode(ParseTree ctx) {
        try {
            return this.visitNode(ctx);
        } catch (ClassCastException e) {
            if (ctx instanceof ParserRuleContext parserRuleContext) {
                throw new TypeMismatchException(parserRuleContext, "Invalid types!");
            } else {
                throw new TypeMismatchException("Invalid types!");
            }
        }
    }

    public <T> T visitNode(ParseTree ctx) {
        try {
            var result = ctx.accept(QilletniVisitor.this);
            if (result == null) {
                return null;
            }

            return (T) result;
        } catch (QilletniException e) {
            if (!e.isSourceSet() && ctx instanceof ParserRuleContext parserRuleContext) {
                e.setSource(parserRuleContext);
            }

            throw e;
        }
    }

    // T can either be QilletniType (defined) or UninitializedType (undefined)
    public record EntityProperty<T>(String name, T type) {}

    public record ConditionalReturning(boolean finishedBranch, Optional<QilletniType> returnValue) {
        ConditionalReturning(boolean finishedBranch) {
            this(finishedBranch, Optional.empty());
        }
    }

}

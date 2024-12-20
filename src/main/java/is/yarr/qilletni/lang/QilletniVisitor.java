package is.yarr.qilletni.lang;

import is.yarr.qilletni.StringUtility;
import is.yarr.qilletni.antlr.QilletniLexer;
import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.antlr.QilletniParserBaseVisitor;
import is.yarr.qilletni.api.exceptions.InvalidWeightException;
import is.yarr.qilletni.api.lang.stack.QilletniStackTrace;
import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.api.lang.table.SymbolTable;
import is.yarr.qilletni.api.lang.types.AlbumType;
import is.yarr.qilletni.api.lang.types.AnyType;
import is.yarr.qilletni.api.lang.types.BooleanType;
import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.lang.types.DoubleType;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.ImportAliasType;
import is.yarr.qilletni.api.lang.types.IntType;
import is.yarr.qilletni.api.lang.types.JavaType;
import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.SongType;
import is.yarr.qilletni.api.lang.types.StringType;
import is.yarr.qilletni.api.lang.types.WeightsType;
import is.yarr.qilletni.api.lang.types.collection.CollectionLimit;
import is.yarr.qilletni.api.lang.types.collection.CollectionLimitUnit;
import is.yarr.qilletni.api.lang.types.collection.CollectionOrder;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinition;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.api.lang.types.entity.UninitializedType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.api.lang.types.weights.WeightEntry;
import is.yarr.qilletni.api.lang.types.weights.WeightUnit;
import is.yarr.qilletni.api.lang.types.weights.WeightUtils;
import is.yarr.qilletni.api.music.MusicPopulator;
import is.yarr.qilletni.api.music.supplier.DynamicProvider;
import is.yarr.qilletni.lang.exceptions.CannotTypeCheckAnyException;
import is.yarr.qilletni.lang.exceptions.CascadeFailedException;
import is.yarr.qilletni.lang.exceptions.FunctionDidntReturnException;
import is.yarr.qilletni.lang.exceptions.FunctionInvocationException;
import is.yarr.qilletni.lang.exceptions.InvalidConstructor;
import is.yarr.qilletni.lang.exceptions.InvalidStaticException;
import is.yarr.qilletni.lang.exceptions.InvalidSyntaxException;
import is.yarr.qilletni.lang.exceptions.ListOutOfBoundsException;
import is.yarr.qilletni.lang.exceptions.QilletniContextException;
import is.yarr.qilletni.lang.exceptions.TypeMismatchException;
import is.yarr.qilletni.lang.exceptions.VariableNotFoundException;
import is.yarr.qilletni.lang.internal.FunctionInvokerImpl;
import is.yarr.qilletni.lang.internal.NativeFunctionHandler;
import is.yarr.qilletni.lang.table.SymbolImpl;
import is.yarr.qilletni.lang.table.TableUtils;
import is.yarr.qilletni.lang.types.AlbumTypeImpl;
import is.yarr.qilletni.lang.types.BooleanTypeImpl;
import is.yarr.qilletni.lang.types.CollectionTypeImpl;
import is.yarr.qilletni.lang.types.DoubleTypeImpl;
import is.yarr.qilletni.lang.types.EntityTypeImpl;
import is.yarr.qilletni.lang.types.FunctionTypeImpl;
import is.yarr.qilletni.lang.types.IntTypeImpl;
import is.yarr.qilletni.lang.types.JavaTypeImpl;
import is.yarr.qilletni.lang.types.ListTypeImpl;
import is.yarr.qilletni.lang.types.SongTypeImpl;
import is.yarr.qilletni.lang.types.StringTypeImpl;
import is.yarr.qilletni.lang.types.TypeUtils;
import is.yarr.qilletni.lang.types.WeightsTypeImpl;
import is.yarr.qilletni.lang.types.entity.EntityAttributes;
import is.yarr.qilletni.lang.types.entity.EntityDefinitionImpl;
import is.yarr.qilletni.lang.types.entity.UninitializedTypeImpl;
import is.yarr.qilletni.lang.types.list.ListTypeTransformer;
import is.yarr.qilletni.lang.types.weights.LazyWeightEntry;
import is.yarr.qilletni.lang.types.weights.WeightEntryImpl;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class QilletniVisitor extends QilletniParserBaseVisitor<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QilletniVisitor.class);

    private final SymbolTable symbolTable;
    private final Scope globalScope;
    private final DynamicProvider dynamicProvider;
    private final EntityDefinitionManager entityDefinitionManager;
    private final MusicPopulator musicPopulator;
    private final ListTypeTransformer listTypeTransformer;
    // The file, the namespace
    private final BiFunction<String, String, Optional<ImportAliasType>> importConsumer;
    private final FunctionInvokerImpl functionInvoker;
    private final QilletniStackTrace qilletniStackTrace;

    public QilletniVisitor(SymbolTable symbolTable, Map<SymbolTable, QilletniVisitor> symbolTableMap, Scope globalScope, DynamicProvider dynamicProvider, EntityDefinitionManager entityDefinitionManager, NativeFunctionHandler nativeFunctionHandler, MusicPopulator musicPopulator, ListTypeTransformer listTypeTransformer, QilletniStackTrace qilletniStackTrace, BiFunction<String, String, Optional<ImportAliasType>> importConsumer) {
        this.symbolTable = symbolTable;
        this.globalScope = globalScope;
        this.dynamicProvider = dynamicProvider;
        this.entityDefinitionManager = entityDefinitionManager;
        this.musicPopulator = musicPopulator;
        this.listTypeTransformer = listTypeTransformer;
        this.importConsumer = importConsumer;
        this.qilletniStackTrace = qilletniStackTrace;
        this.functionInvoker = new FunctionInvokerImpl(symbolTable, symbolTableMap, nativeFunctionHandler, qilletniStackTrace);
    }

    @Override
    public Object visitProg(QilletniParser.ProgContext ctx) {
        symbolTable.initScope(globalScope);
        ctx.children.forEach(this::visitNode);
        return null;
    }

    @Override
    public Object visitFunction_def(QilletniParser.Function_defContext ctx) {
        var currScope = symbolTable.currentScope();
        
        if (ctx.STATIC() != null) {
            throw new InvalidStaticException(ctx, "Cannot define static function outside of an entity");
        }
        
        scopedVisitFunctionDef(currScope, ctx);
        return null;
    }

    private void scopedVisitFunctionDef(Scope scope, QilletniParser.Function_defContext ctx) {
        QilletniTypeClass<?> onType = null;
        if (ctx.function_on_type() != null) {
            onType = visitNode(ctx.function_on_type());
        }

        scopedDefineFunction(scope, ctx, onType, onType, onType != null); // only externally defined if has an on type
    }

    private void scopedVisitEntityFunctionDef(Scope scope, QilletniParser.Function_defContext ctx, QilletniTypeClass<?> onType) {
        scopedDefineFunction(scope, ctx, onType, onType, false);
    }

    private void scopedDefineFunction(Scope scope, QilletniParser.Function_defContext ctx, QilletniTypeClass<?> implOnType, QilletniTypeClass<?> nativeOnType, boolean isExternallyDefined) {
        var id = ctx.ID().getText();

        LOGGER.debug("Defining func of {}", id);
        LOGGER.debug("on scope: {}", scope);

        var params = new ArrayList<String>(visitNode(ctx.function_def_params()));

        int definedParamCount = params.size();
        
        var isStatic = ctx.STATIC() != null;

        if (ctx.NATIVE() != null) {
            int invokingParamCount = params.size();
            
            if (implOnType != null) {
                definedParamCount++;
            }
            
            // If it is native, force it to have the on type of the entity
            scope.defineFunction(SymbolImpl.createFunctionSymbol(id, FunctionTypeImpl.createNativeFunction(id, params.toArray(String[]::new), invokingParamCount, definedParamCount, isExternallyDefined, nativeOnType, isStatic)));
        } else {
            int invokingParamCount = params.size();
            if (implOnType != null && isExternallyDefined) {
                invokingParamCount--;
            }
            
            LOGGER.debug("{} params = {} (defined: {}) on type = {}", id, params, definedParamCount, implOnType);
            scope.defineFunction(SymbolImpl.createFunctionSymbol(id, FunctionTypeImpl.createImplementedFunction(id, params.toArray(String[]::new), invokingParamCount, definedParamCount, isExternallyDefined, implOnType, isStatic, ctx.body())));
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
    public QilletniType visitAsmt(QilletniParser.AsmtContext ctx) {
        var id = ctx.ID(ctx.ID().size() - 1).getText();

        var currentScope = symbolTable.currentScope();

        if (ctx.type != null && ctx.LEFT_SBRACKET() != null) { // defining a new list
            var expr = ctx.expr(0);
            ListType assignmentValue = switch (ctx.type.getType()) {
                case QilletniLexer.ANY_TYPE -> createListOfTypeFromExpression(expr, QilletniTypeClass.ANY); // TODO: implement ANY
                case QilletniLexer.INT_TYPE -> createListOfTypeFromExpression(expr, QilletniTypeClass.INT);
                case QilletniLexer.DOUBLE_TYPE -> createListOfTypeFromExpression(expr, QilletniTypeClass.DOUBLE);
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

            LOGGER.debug("(new) {}[{}] = {}", id, assignmentValue.getTypeClass(), assignmentValue);
            currentScope.define(SymbolImpl.createGenericSymbol(id, assignmentValue.getTypeClass(), assignmentValue));

            // visitQilletniList
        } else if (ctx.LEFT_SBRACKET() != null) { // foo[123] = expr
            var listSymbol = currentScope.<ListTypeImpl>lookup(id);
            var list = listSymbol.getValue();
            var index = visitQilletniTypedNode(ctx.int_expr(), IntTypeImpl.class).getValue();

            if (index < 0 || index > list.getItems().size()) {
                throw new ListOutOfBoundsException(ctx, "Attempted to access index %d on a list with a size of %d".formatted(index, list.getItems().size()));
            }

            var expressionValue = visitQilletniTypedNode(ctx.expr(0));
            if (!expressionValue.getTypeClass().equals(list.getSubType())) {
                throw new TypeMismatchException(ctx, "Attempted to assign a %s in a %s list".formatted(expressionValue.typeName(), list.typeName()));
            }

            var mutableItems = new ArrayList<>(list.getItems());
            mutableItems.set((int) index, expressionValue);
            list.setItems(mutableItems);

            listSymbol.setValue(list); // Not really needed
        } else if (ctx.type != null) { // defining a new var
            var expr = ctx.expr(0);
            QilletniTypeClass<?> variableType = null;
            QilletniType assignmentValue = switch (ctx.type.getType()) {
                case QilletniLexer.ANY_TYPE -> {
                    variableType = QilletniTypeClass.ANY;
                    yield visitQilletniTypedNode(expr, AnyType.class); // TODO: Should this set AnyType.class?
                }
                case QilletniLexer.INT_TYPE -> visitQilletniTypedNode(expr, IntType.class);
                case QilletniLexer.DOUBLE_TYPE -> {
                    var value = visitQilletniTypedNode(expr);
                    
                    if (!(value instanceof IntType intType)) {
                        yield TypeUtils.safelyCast(value, DoubleType.class);
                    }
                    
                    yield new DoubleTypeImpl(intType.getValue());
                }
                case QilletniLexer.BOOLEAN_TYPE -> visitQilletniTypedNode(expr, BooleanType.class);
                case QilletniLexer.STRING_TYPE -> visitQilletniTypedNode(expr, StringType.class);
                case QilletniLexer.COLLECTION_TYPE -> {
                    var value = visitQilletniTypedNode(expr);
                    LOGGER.debug("value = {}", value);
                    if (!(value instanceof StringType stringType)) {
                        yield TypeUtils.safelyCast(value, CollectionType.class);
                    }

                    yield musicPopulator.initiallyPopulateCollection(new CollectionTypeImpl(stringType.stringValue()));
                }
                case QilletniLexer.SONG_TYPE -> {
                    var value = visitQilletniTypedNode(expr);
                    if (!(value instanceof StringType stringType)) {
                        yield TypeUtils.safelyCast(value, SongType.class);
                    }

                    yield musicPopulator.initiallyPopulateSong(new SongTypeImpl(stringType.stringValue()));
                }
                case QilletniLexer.ALBUM_TYPE -> {
                    var value = visitQilletniTypedNode(expr);
                    if (!(value instanceof StringType stringType)) {
                        yield TypeUtils.safelyCast(value, AlbumType.class);
                    }
                    
                    yield musicPopulator.initiallyPopulateAlbum(new AlbumTypeImpl(stringType.stringValue()));
                }
                case QilletniLexer.WEIGHTS_KEYWORD -> visitQilletniTypedNode(expr, WeightsTypeImpl.class);
                case QilletniLexer.ID -> {
                    var entityName = ctx.type.getText();

                    var expectedEntity = entityDefinitionManager.lookup(entityName);
                    var entityNode = visitQilletniTypedNode(expr, EntityType.class);

                    var gotTypeName = entityNode.getEntityDefinition().getTypeName();
                    if (!entityNode.getEntityDefinition().equals(expectedEntity)) {
                        throw new TypeMismatchException(ctx, "Expected entity " + entityName + ", got " + gotTypeName);
                    }

                    yield entityNode;
                }
                default -> throw new RuntimeException("This should not be possible, unknown type");
            };
            
            if (variableType == null) { // It's only set before this if it's ANY
                variableType = assignmentValue.getTypeClass();
            }

            LOGGER.debug("(new) {} = {}", id, assignmentValue);
            currentScope.define(SymbolImpl.createGenericSymbol(id, variableType, assignmentValue));

            if (assignmentValue instanceof EntityType entityType) {
                return entityType;
            }
        } else if (ctx.DOUBLE_DOT() != null) {
            // cascade, either with expr or asmt to the left. If asmt is to the left,
            // that asmt SHOULD return the Entity to change the value of
            
            if (id.startsWith("_")) {
                throw new VariableNotFoundException(ctx.expr_assign, "Cannot access private variable");
            }
            
            var entityNode = ctx.asmt() != null ? ctx.asmt() : ctx.expr(0);
            
            var visitedEntity = visitNode(entityNode);
            if (!(visitedEntity instanceof EntityType entity)) {
                throw new CascadeFailedException(entityNode);
            }
            
            var propertyValue = visitQilletniTypedNode(ctx.expr(ctx.expr().size() - 1));
            var entityScope = entity.getEntityScope();
            entityScope.lookup(id).setValue(propertyValue);

            return entity;
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

        BiFunction<Long, Long, Long> operation = (ctx.INCREMENT(0) != null || ctx.PLUS_EQUALS() != null) ? ((a, b) -> a + b) : ((a, b) -> a - b);
        
        if (ctx.post_crement != null && ctx.pre_crement != null) {
            throw new InvalidSyntaxException("Cannot have one increment or decrements at a time");
        }
        
        if (ctx.BOOL() != null) {
            return new BooleanTypeImpl(ctx.BOOL().getText().equals("true"));
        }
        
        if (ctx.NOT() != null) {
            return new BooleanTypeImpl(!visitQilletniTypedNode(ctx.expr(0), BooleanType.class).getValue());
        }
        
        if (ctx.ID() != null) {
            var idText = ctx.ID().getText();

            if (ctx.LEFT_SBRACKET() != null) { // foo[123]
                var list = currentScope.<ListTypeImpl>lookup(idText).getValue();
                var index = visitQilletniTypedNode(ctx.expr(0), IntTypeImpl.class).getValue();

                if (index < 0 || index > list.getItems().size()) {
                    throw new ListOutOfBoundsException(ctx, "Attempted to access index " + index + " on a list with a size of " + list.getItems().size());
                }

                if ((ctx.post_crement != null || ctx.pre_crement != null || ctx.post_crement_equals != null) && !list.getSubType().equals(QilletniTypeClass.INT)) {
                    throw new TypeMismatchException("Cannot increment/decrement from a " + list.getSubType().getTypeName() + "[]");
                }
                
                if (ctx.post_crement != null || ctx.post_crement_equals != null) {
                    var incrementBy = 1L;
                    if (ctx.post_crement_equals != null) {
                        incrementBy = visitQilletniTypedNode(ctx.expr(1), IntType.class).getValue();
                    }
                    
                    var item = (IntType) list.getItems().get((int) index);
                    var oldVal = item.getValue();
                    list.getItems().set((int) index, new IntTypeImpl(operation.apply(oldVal, incrementBy)));
                    return new IntTypeImpl(oldVal);
                }
                
                if (ctx.pre_crement != null) {
                    var item = (IntType) list.getItems().get((int) index);
                    var newItem = new IntTypeImpl(operation.apply(item.getValue(), 1L));
                    list.getItems().set((int) index, newItem);
                    return newItem;
                }

                return list.getItems().get((int) index);
            }

            if (ctx.DOT() == null) { // id
                LOGGER.debug("Visiting expr! ID: {}", ctx.ID());
                LOGGER.debug("with current scope: {}", symbolTable);
                
                // For static method access
                if (entityDefinitionManager.isDefined(idText)) {
                    return entityDefinitionManager.lookup(idText).createStaticInstance();
                }
                
                var variableSymbol = currentScope.lookup(idText);
                var variable = variableSymbol.getValue();

//                System.out.println("variableSymbol = " + variableSymbol);
                
                if ((ctx.post_crement != null || ctx.pre_crement != null || ctx.post_crement_equals != null) && !variable.getTypeClass().equals(QilletniTypeClass.INT)) {
                    throw new TypeMismatchException("Cannot increment/decrement from a " + variable.getTypeClass().getTypeName());
                }

                if (ctx.post_crement != null || ctx.post_crement_equals != null) {
                    var incrementBy = 1L;
                    if (ctx.post_crement_equals != null) {
                        incrementBy = visitQilletniTypedNode(ctx.expr(0), IntType.class).getValue();
                    }
                    
                    var intVar = (IntType) variable;
                    var oldVal = intVar.getValue();
                    variableSymbol.setValue(new IntTypeImpl(operation.apply(oldVal, incrementBy)));
                    return new IntTypeImpl(oldVal);
                }

                if (ctx.pre_crement != null) {
                    var intVar = (IntType) variable;
                    var newVal = new IntTypeImpl(operation.apply(intVar.getValue(), 1L));
                    variableSymbol.setValue(newVal);
                    return newVal;
                }
                
                return variable;
            } else { // foo.baz
                if (idText.startsWith("_")) {
                    throw new VariableNotFoundException(ctx, "Cannot access private variable");
                }
                
                var visitedNode = visitQilletniTypedNode(ctx.expr(0));
                
                var scope = switch (visitedNode) {
                    case EntityType entityType -> entityType.getEntityScope();
                    case ImportAliasType importAliasType -> importAliasType.getScope();
                    default -> throw new TypeMismatchException("Can only access property of entity or import alias");
                };
                
//                LOGGER.debug("Getting property {} on entity {}", idText, entity.typeName());
                var propertySymbol = scope.lookup(idText);
                var property = propertySymbol.getValue();

                if ((ctx.post_crement != null || ctx.post_crement_equals != null) && !property.getTypeClass().equals(QilletniTypeClass.INT)) {
                    throw new TypeMismatchException("Cannot increment/decrement from a " + property.getTypeClass().getTypeName());
                }

                if (ctx.post_crement != null || ctx.post_crement_equals != null) {
                    var incrementBy = 1L;
                    if (ctx.post_crement_equals != null) {
                        incrementBy = visitQilletniTypedNode(ctx.expr(1), IntType.class).getValue();
                    }
                    
                    var intVar = (IntType) property;
                    var newVal = new IntTypeImpl(operation.apply(intVar.getValue(), incrementBy));
                    propertySymbol.setValue(newVal);
                    
                    return new IntTypeImpl(intVar.getValue());
                }
                
                return property;
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
                double left;
                double right;
                
                if (leftType instanceof IntType intType) {
                    left = intType.getValue();
                } else if (leftType instanceof DoubleType doubleType) {
                    left = doubleType.getValue();
                } else {
                    throw new TypeMismatchException(leftChild, "Can only compare number types! Left param is a %s".formatted(leftType.typeName()));
                }
                
                if (rightType instanceof IntType intType) {
                    right = intType.getValue();
                } else if (rightType instanceof DoubleType doubleType) {
                    right = doubleType.getValue();
                } else {
                    throw new TypeMismatchException(rightChild, "Can only compare number types! Right param is a %s".formatted(rightType.typeName()));
                }

                LOGGER.debug("Comparing {} {} {}", left, relOpVal, right);

                var comparisonResult = switch (relOpVal) {
                    case ">" -> left > right;
                    case "<" -> left < right;
                    case "<=" -> left <= right;
                    case ">=" -> left >= right;
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
            LOGGER.debug("leftExpr = {}", leftExpr);
            return functionInvoker.invokeFunction(ctx.function_call(), leftExpr).orElseThrow(FunctionDidntReturnException::new);
        }

        if (ctx.LEFT_PAREN() != null) { // ( expr )
            return visitQilletniTypedNode(ctx.getChild(1));
        }

        if (ctx.op != null) { // id + id
            var leftExpr = visitQilletniTypedNode(ctx.expr(0));
            var rightExpr = visitQilletniTypedNode(ctx.expr(1));
            
            var biOperation = ctx.op.getText();
            
            // Handle + different, as multiple types can be added together
            if (biOperation.equals("+")) {
                if (leftExpr instanceof IntType leftInt && rightExpr instanceof IntType rightInt) {
                    var leftValue = leftInt.getValue();
                    var rightValue = rightInt.getValue();

                    return new IntTypeImpl(leftValue + rightValue);
                } else if ((leftExpr instanceof IntType && rightExpr instanceof DoubleType) || (leftExpr instanceof DoubleType && rightExpr instanceof IntType) || (leftExpr instanceof DoubleType && rightExpr instanceof DoubleType)) {
                    double leftValue = leftExpr instanceof IntType intType ? intType.getValue() : ((DoubleType) leftExpr).getValue();
                    double rightValue = rightExpr instanceof IntType intType ? intType.getValue() : ((DoubleType) rightExpr).getValue();

                    return new DoubleTypeImpl(leftValue + rightValue);
                }

                return new StringTypeImpl(leftExpr.stringValue() + rightExpr.stringValue());
            }

            BiFunction<Number, Number, Number> calculate = switch (biOperation) {
                case "*" -> (a, b) -> (a instanceof Double || b instanceof Double) ? a.doubleValue() * b.doubleValue() : a.longValue() * b.longValue();
                case "/~" -> (a, b) -> (a instanceof Double || b instanceof Double) ? (long) (a.doubleValue() / b.doubleValue()) : a.longValue() / b.longValue();  // Integer division
                case "/" -> (a, b) -> a.doubleValue() / b.doubleValue();  // Normal division
                case "%" -> (a, b) -> (a instanceof Double || b instanceof Double) ? a.doubleValue() % b.doubleValue() : a.longValue() % b.longValue();
                case "-" -> (a, b) -> (a instanceof Double || b instanceof Double) ? a.doubleValue() - b.doubleValue() : a.longValue() - b.longValue();
                default -> throw new IllegalStateException("Unexpected value: %s".formatted(ctx.OP().getText()));
            };

            Number leftVal = leftExpr instanceof IntType intType ? intType.getValue() : ((DoubleType) leftExpr).getValue();
            Number rightVal = rightExpr instanceof IntType intType ? intType.getValue() : ((DoubleType) rightExpr).getValue();

            if (!biOperation.equals("/~") && (leftVal instanceof Double || rightVal instanceof Double)) {
                return new DoubleTypeImpl(calculate.apply(leftVal, rightVal).doubleValue());
            } else {
                return new IntTypeImpl(calculate.apply(leftVal, rightVal).longValue());
            }
        }

        if (ctx.function_call() != null) {
            return functionInvoker.invokeFunction(ctx.function_call()).orElseThrow(FunctionDidntReturnException::new);
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
            value = this.functionInvoker.<StringType>invokeFunction(functionCallContext).orElseThrow(FunctionDidntReturnException::new);
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

        if (ctx.wrap != null) {
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
            } else if (type == QilletniLexer.INT_TYPE) {
                value = new IntTypeImpl((int) visitQilletniTypedNode(ctx.double_expr(), DoubleType.class).getValue());
            }
        } else if (child instanceof QilletniParser.Function_callContext functionCallContext) {
            value = this.functionInvoker.<IntType>invokeFunction(functionCallContext).orElseThrow(FunctionDidntReturnException::new);
        } else if (child instanceof QilletniParser.Int_exprContext) {
            value = visitQilletniTypedNode(child);
        }

        if (ctx.op != null) {
            var operation = ctx.op.getText();
            BiFunction<Long, Long, Long> calculate = switch (operation) {
                case "*" -> (a, b) -> a * b;
                case "/~" -> (a, b) -> a / b;
                case "%" -> (a, b) -> a % b;
                case "+" -> (a, b) -> a + b;
                case "-" -> (a, b) -> a - b;
                default -> throw new IllegalStateException("Unexpected value: " + ctx.OP().getText());
            };

            var intVal = 0L;
            if (value != null) {
                intVal = value.getValue();
            }

            IntType add = visitQilletniTypedNode(ctx.getChild(2));
            value = new IntTypeImpl(calculate.apply(intVal, add.getValue()));
        }

        return value;
    }

    @Override
    public DoubleType visitDouble_expr(QilletniParser.Double_exprContext ctx) {
        var child = ctx.getChild(0);
        
        if (ctx.wrap != null) {
            child = ctx.getChild(1);
        }

        if (child instanceof TerminalNode terminalNode) {
            var symbol = terminalNode.getSymbol();
            var type = symbol.getType();
            if (type == QilletniLexer.ID) {
                return symbolTable.currentScope().<DoubleType>lookup(symbol.getText()).getValue();
            } else if (type == QilletniLexer.DOUBLE) {
                return new DoubleTypeImpl(Double.parseDouble(symbol.getText().replace("D", "")));
            } else if (type == QilletniLexer.DOUBLE_TYPE) {
                return new DoubleTypeImpl(visitQilletniTypedNode(ctx.int_expr(0), IntType.class).getValue());
            }
        } else if (child instanceof QilletniParser.Function_callContext functionCallContext) {
            return this.functionInvoker.<DoubleType>invokeFunction(functionCallContext).orElseThrow(FunctionDidntReturnException::new);
        }
        
        if (ctx.ii_op != null) {
            var firstNum = visitQilletniTypedNode(ctx.int_expr(0), IntType.class).getValue();
            var secondNum = visitQilletniTypedNode(ctx.int_expr(1), IntType.class).getValue();
            return new DoubleTypeImpl(((double) firstNum) / ((double) secondNum));
        }

        Function<String, BiFunction<Double, Double, Double>> calculateFunction = op -> switch (op) {
            case "*" -> (a, b) -> a * b;
            case "/~" -> (a, b) -> (double) ((int) (a / b));
            case "/" -> (a, b) -> a / b;
            case "%" -> (a, b) -> a % b;
            case "+" -> (a, b) -> a + b;
            case "-" -> (a, b) -> a - b;
            default -> throw new IllegalStateException("Unexpected value: " + ctx.OP().getText());
        };

        double firstNum = 0;
        double secondNum = 0;
        BiFunction<Double, Double, Double> calculate = null;
        
        if (ctx.dd_op != null) {
            calculate = calculateFunction.apply(ctx.dd_op.getText());

            firstNum = visitQilletniTypedNode(ctx.double_expr(0), DoubleType.class).getValue();
            secondNum = visitQilletniTypedNode(ctx.double_expr(1), DoubleType.class).getValue();
        }
        
        if (ctx.di_op != null) {
            calculate = calculateFunction.apply(ctx.di_op.getText());

            firstNum = visitQilletniTypedNode(ctx.double_expr(0), DoubleType.class).getValue();
            secondNum = visitQilletniTypedNode(ctx.int_expr(0), IntType.class).getValue();
        }
        
        if (ctx.id_op != null) {
            calculate = calculateFunction.apply(ctx.id_op.getText());

            firstNum = visitQilletniTypedNode(ctx.int_expr(0), IntType.class).getValue();
            secondNum = visitQilletniTypedNode(ctx.double_expr(0), DoubleType.class).getValue();
        }
        
        return new DoubleTypeImpl(calculate.apply(firstNum, secondNum));
    }

    @Override
    public ListType visitList_expression(QilletniParser.List_expressionContext ctx) {
        return createListOfAnyType(ctx);
    }

    @Override
    public BooleanType visitIs_expr(QilletniParser.Is_exprContext ctx) {
        var checkingVariable = symbolTable.currentScope().lookup(ctx.ID().getFirst().getText()).getValue();
        
        // If checking a bracket, type is LIST. Otherwise, `type` is defined and get type class from there
        var type = ctx.LEFT_SBRACKET() != null ? QilletniTypeClass.LIST :
                TypeUtils.getTypeFromStringOrEntity(ctx.type.getText());
        
        if (type.equals(QilletniTypeClass.ANY)) {
            throw new CannotTypeCheckAnyException(ctx, "Cannot type check against 'any'");
        }
        
        if (ctx.ID().size() == 2) { // Entity name
            if (!(checkingVariable instanceof EntityType entityType)) {
                return BooleanTypeImpl.FALSE;
            }
            
            var expectedEntity = entityDefinitionManager.lookup(ctx.ID(1).getText());

            var gotTypeName = entityType.getEntityDefinition();
            if (!entityType.getEntityDefinition().equals(expectedEntity)) {
                LOGGER.debug("Expected entity {}, got {}", expectedEntity.getTypeName(), gotTypeName.getTypeName());
                return BooleanTypeImpl.FALSE;
            }
            
            LOGGER.debug("they equal!");
            return BooleanTypeImpl.TRUE;
        }
        
        return new BooleanTypeImpl(type.isAssignableFrom(checkingVariable.getTypeClass()));
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
            return Optional.of(ListTypeImpl.emptyList());
        }

        return Optional.empty();
    }
    
    private Optional<ListType> checkEmptyTypedList(QilletniParser.List_expressionContext ctx, QilletniTypeClass<?> listType) {
        if (ctx.expr_list() == null) {
            return Optional.of(new ListTypeImpl(listType, Collections.emptyList()));
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
            QilletniTypeClass<?> listType = QilletniTypeClass.ANY;
            if (!typeList.isEmpty()) {
                listType = typeList.getFirst();
            }

            return new ListTypeImpl(listType, items);
        });
    }
    
    private <T extends QilletniType> ListType createListOfType(QilletniParser.List_expressionContext ctx, QilletniTypeClass<T> listType) {
        return checkEmptyTypedList(ctx, listType).or(() -> checkAlreadyComputedList(ctx)).orElseGet(() -> {
            var items = this.<List<QilletniType>>visitNode(ctx.expr_list());

            var transformedItems = items.stream().map(listItem -> {
                if (listType.isAssignableFrom(listItem.getTypeClass())) { // This means if listType is ANY, no transformations happen
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
            // Just assume it's a list
//            var exp = exprCtx.getChild(QilletniParser.ExprContext.class, 0);
//            if (exp == null) {
//                throw new TypeMismatchException(exprCtx, "Expected list expression.");
//            }
            
            var list = visitQilletniTypedNode(exprCtx, ListType.class);
            if (!listType.isAssignableFrom(list.getSubType())) {
                throw new TypeMismatchException("Expected list of type %s, got %s".formatted(listType.getTypeName(), list.getSubType().getTypeName()));
            }
            
            return list;
        }

        return createListOfType(ctx, listType);
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

        if (ctx.stmt() != null) {
            visitNode(ctx.stmt());
        }

        if (ctx.expr() != null) {
            visitNode(ctx.expr());
        }
        
        return Optional.empty();
    }

    @Override
    public Object visitStmt(QilletniParser.StmtContext ctx) {
        if (ctx.DOT() != null) { // foo.bar()
            var leftExpr = visitQilletniTypedNode(ctx.expr());
            functionInvoker.invokeFunction(ctx.function_call(), leftExpr);
            return null;
        }

        visitChildren(ctx);
        return null;
    }

    @Override
    public Optional<QilletniType> visitFunction_call(QilletniParser.Function_callContext ctx) {
        return functionInvoker.invokeFunction(ctx);
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
        symbolTable.pushScope(); // The scope for only holding the incrementing var
        var iteratingListState = new AtomicReference<ListType>(null); // A state that holds the list that is being iterated (if in a foreach)
        var iteratingIntState = new AtomicReference<IntType>(null); // A state that holds the "to" value of a range

        for (int i = 0; visitForExpression(ctx.for_expr(), i, iteratingListState, iteratingIntState).getValue(); i++) {
            symbolTable.pushScope(); // The actual inner for loop scope that gets reset every iteration
            Optional<QilletniType> bodyReturn = visitNode(ctx.body());

            symbolTable.popScope();
            if (bodyReturn.isPresent()) {
                return bodyReturn;
            }
        }

        symbolTable.popScope();

        return Optional.empty();
    }

    /**
     * @param ctx
     * @param index
     * @param iteratingListState
     * @return If the for loop should iterate
     */
    public BooleanType visitForExpression(QilletniParser.For_exprContext ctx, int index, AtomicReference<ListType> iteratingListState, AtomicReference<IntType> iteratingIntState) {
        if (ctx.expr() != null) {
            return visitQilletniTypedNode(ctx.expr(), BooleanType.class);
        } else if (ctx.range() != null) {
            LOGGER.debug("range = {}", ctx.range().getText());
            return visitRange(ctx.range(), iteratingIntState);
        } else if (ctx.foreach_range() != null) {
            return visitForeachRange(ctx.foreach_range(), index, iteratingListState);
        }

        // Should never happen
        return BooleanTypeImpl.FALSE;
    }

    @Override
    public BooleanType visitFor_expr(QilletniParser.For_exprContext ctx) {
        throw new RuntimeException();
    }

    public BooleanType visitRange(QilletniParser.RangeContext ctx, AtomicReference<IntType> iteratingIntState) {
        var scope = symbolTable.currentScope();
        var id = ctx.ID().getText();

        IntType rangeTo = iteratingIntState.get();
        if (rangeTo == null) {
            rangeTo = ctx.RANGE_INFINITY() != null ? new IntTypeImpl(Integer.MAX_VALUE) : visitQilletniTypedNode(ctx.expr(), IntType.class);
            iteratingIntState.set(rangeTo);
        }

        if (!scope.isDirectlyDefined(id)) { // first iteration, let it pass
            scope.define(new SymbolImpl<>(id, QilletniTypeClass.INT, new IntTypeImpl(0)));
            return BooleanTypeImpl.TRUE;
        }

        var idIntType = scope.<IntType>lookup(id);
        var newValue = idIntType.getValue().getValue() + 1;
        idIntType.setValue(new IntTypeImpl(newValue));

        if (rangeTo.getValue() > newValue) {
            return BooleanTypeImpl.TRUE;
        }

        return BooleanTypeImpl.FALSE;
    }

    @Override
    public BooleanType visitRange(QilletniParser.RangeContext ctx) {
        throw new RuntimeException();
    }

    public BooleanType visitForeachRange(QilletniParser.Foreach_rangeContext ctx, int index, AtomicReference<ListType> iteratingListState) {
        var scope = symbolTable.currentScope();
        var variableName = ctx.ID().getText();
        
        if (iteratingListState.get() == null) {
            iteratingListState.set(visitQilletniTypedNode(ctx.expr(), ListTypeImpl.class));
        }
        
        var list = iteratingListState.get();
        
        if (index >= list.getItems().size()) {
            return BooleanTypeImpl.FALSE;
        }

        var currentItem = list.getItems().get(index);
        LOGGER.debug("currentItem = {}", currentItem);

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
        String importAs = null;

        if (ctx.ID() != null) {
            importAs = ctx.ID().getText();
        }
        
        var importAliasOptional = importConsumer.apply(ctx.STRING().getText(), importAs);

        if (importAliasOptional.isPresent()) {
            var scope = symbolTable.currentScope();
            if (scope.isDirectlyDefined(importAs)) {
                var lookedUp = scope.lookup(importAs);
                if (lookedUp.getType().equals(QilletniTypeClass.IMPORT_ALIAS)) {
                    // TODO: Join import aliases
                }
            }
            scope.define(SymbolImpl.createGenericSymbol(importAs, QilletniTypeClass.IMPORT_ALIAS, importAliasOptional.get()));
        }
        
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
            return this.functionInvoker.<SongType>invokeFunction(ctx.function_call()).orElseThrow(FunctionDidntReturnException::new);
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
            return this.functionInvoker.<AlbumType>invokeFunction(ctx.function_call()).orElseThrow(FunctionDidntReturnException::new);
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

        var pipe = ctx.WEIGHT_PIPE().getText();

        boolean canRepeatTrack = pipe.equals("|!");
        boolean canRepeatWeight = !pipe.equals("|~");
        
        var weightUnit = WeightUnit.fromSymbol(weightAmount.WEIGHT_UNIT().getText());
        
        if (ctx.expr().function_call() != null) {
            return new LazyWeightEntry(weightInt, weightUnit, dynamicProvider, canRepeatTrack, canRepeatWeight, visitQilletniTypedNode(ctx.expr(), SongType.class)::getTrack);
        }
        
        QilletniType weightValue = visitQilletniTypedNode(ctx.expr());
        
        if (weightValue instanceof StringType stringType) {
            weightValue = dynamicProvider.getStringIdentifier().parseString(stringType.getValue())
                    .orElseThrow(() -> new TypeMismatchException("Expected a song, collection, or list for weight value"));
        }

        return switch (weightValue) {
            case CollectionType collectionType -> new WeightEntryImpl(weightInt, weightUnit, dynamicProvider, collectionType, canRepeatTrack, canRepeatWeight);
            case ListType listType -> {
                if (!QilletniTypeClass.SONG.equals(listType.getSubType())) {
                    throw new TypeMismatchException("Expected a song list, got a " + listType.getSubType());
                }

                listType.getItems().stream()
                        .map(SongType.class::cast)
                        .forEach(musicPopulator::populateSong);
                
                yield new WeightEntryImpl(weightInt, weightUnit, dynamicProvider, listType, canRepeatTrack, canRepeatWeight);
            }
            case SongType songType -> new WeightEntryImpl(weightInt, weightUnit, dynamicProvider, songType, canRepeatTrack, canRepeatWeight);
            case WeightsType weightsType -> {
                var totalPercent = WeightUtils.validateWeights(weightsType);
                if (totalPercent != 100) {
                    throw new InvalidWeightException("Nested weights must have percentage values adding up to 100%");
                }
                
                yield new WeightEntryImpl(weightInt, weightUnit, dynamicProvider, weightsType, canRepeatTrack, canRepeatWeight);
            }
            default -> throw new TypeMismatchException("Expected a song, collection, or list for weight value, got " + weightValue.getTypeClass());
        };
    }

    @Override
    public Object visitPlay_stmt(QilletniParser.Play_stmtContext ctx) {
        final var trackOrchestrator = dynamicProvider.getTrackOrchestrator();
        
        QilletniType playingNode;
        
        if (ctx.ID() != null) {
            var scope = symbolTable.currentScope();
            playingNode = scope.lookup(ctx.ID().getText()).getValue();
        } else {
            playingNode = visitQilletniTypedNode(ctx.expr());
        }
        
        if (playingNode instanceof SongType song) {
            musicPopulator.populateSong(song);
            trackOrchestrator.playTrack(song.getTrack());
            return null;
        }

        var collection = (CollectionType) playingNode;

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
    public Object visitProvider_stmt(QilletniParser.Provider_stmtContext ctx) {
        var providerName = visitQilletniTypedNode(ctx.str_expr(), StringType.class).getValue();
        
        var currentProviderName = dynamicProvider.getCurrentProvider().getName();
        
        dynamicProvider.switchProvider(providerName);
        
        if (ctx.body() != null) {
            var retValue = visitNode(ctx.body());

            dynamicProvider.switchProvider(currentProviderName);
            
            return retValue;
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
            return this.functionInvoker.<JavaType>invokeFunction(ctx.function_call()).orElseThrow(FunctionDidntReturnException::new);
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
        var entityDefinition = new EntityDefinitionImpl(functionInvoker, entityName, attributes.properties(), attributes.constructorParams(), attributes.entityFunctionPopulators(), scope);
        LOGGER.debug("Define entity: {}", entityName);
        entityDefinitionManager.defineEntity(entityDefinition);

        return entityDefinition;
    }

    private EntityAttributes createEntityBody(QilletniParser.Entity_bodyContext ctx, String entityName) {
        var initializedProperties = new HashMap<String, Supplier<QilletniType>>();
        var unorderedUninitializedProperties = new HashMap<String, UninitializedType>();

        for (var entityProp : ctx.entity_property_declaration()) {
            if (entityProp.expr() == null) {
                if (visitNode(entityProp) instanceof EntityProperty<?>(var name, UninitializedType type)) {
                    unorderedUninitializedProperties.put(name, type);
                } // else should never happen
            } else {
                initializedProperties.put(getEntityPropertyName(entityProp), () -> (QilletniType) this.<EntityProperty<?>>visitNode(entityProp).type());
            }
        }
        
        List<String> params = ctx.entity_constructor() == null ? Collections.emptyList() : visitNode(ctx.entity_constructor());
        if (params.size() != unorderedUninitializedProperties.size() || !params.stream().allMatch(unorderedUninitializedProperties::containsKey)) {
            throw new InvalidConstructor(ctx, "Constructor parameters must match uninitialized properties of the entity");
        }

        // In the same order as the constructor
        var uninitializedProperties = params.stream().collect(Collectors.toMap(Function.identity(),
                unorderedUninitializedProperties::get, (o1, o2) -> o1, LinkedHashMap::new));

        // We don't have the actual QilletniTypeClass for the entity yet, so use a placeholder type
        var onType = QilletniTypeClass.createEntityTypePlaceholder(entityName);
        
        List<EntityDefinition.FunctionPopulator> functionPopulators = ctx.function_def()
                .stream()
                .map(functionDef -> new EntityDefinition.FunctionPopulator(functionDef.STATIC() != null,
                        (Scope scope) -> scopedVisitEntityFunctionDef(scope, functionDef, onType)))
                .toList();

        return new EntityAttributes(initializedProperties, uninitializedProperties, functionPopulators);
    }

    @Override
    public EntityAttributes visitEntity_body(QilletniParser.Entity_bodyContext ctx) {
        throw new RuntimeException("This should never be invoked directly");
    }
    
    private String getEntityPropertyName(QilletniParser.Entity_property_declarationContext ctx) {
        return ctx.ID(ctx.ID().size() - 1).getText();
    }

    @Override
    public EntityProperty<?> visitEntity_property_declaration(QilletniParser.Entity_property_declarationContext ctx) {
        var text = getEntityPropertyName(ctx);
        var type = ctx.type;
        var expr = ctx.expr();

        if (expr == null) {
            // undefined property
            if (ctx.ID().size() == 2) { // is an Entity
                return new EntityProperty<>(text, new UninitializedTypeImpl(entityDefinitionManager.lookup(ctx.ID(0).getText()))); // pass the entity name? TODO
            }
            
            QilletniTypeClass<?> propertyType = TypeUtils.getTypeFromStringOrThrow(type.getText());
            
            if (ctx.LEFT_SBRACKET() != null) { // list property
                propertyType = QilletniTypeClass.createListOfType(propertyType);
            }

            return new EntityProperty<>(text, new UninitializedTypeImpl(propertyType));
        }

        // is a defined property
        
        if (ctx.LEFT_SBRACKET() != null) { // list property
            // TODO: remove duplicate code in visitAsmt
            var value = switch (ctx.type.getType()) {
                case QilletniLexer.ANY_TYPE -> createListOfTypeFromExpression(expr, QilletniTypeClass.ANY); // TODO: implement ANY
                case QilletniLexer.INT_TYPE -> createListOfTypeFromExpression(expr, QilletniTypeClass.INT);
                case QilletniLexer.DOUBLE_TYPE -> createListOfTypeFromExpression(expr, QilletniTypeClass.DOUBLE);
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
                    if (!listSubtypeClass.isAssignableFrom(expectedEntity)) {
                        var gotTypeName = listSubtypeClass.getTypeName();
                        throw new TypeMismatchException(ctx.expr(), "Expected entity %s, got %s".formatted(entityName, gotTypeName));
                    }

                    yield entityNode;
                }
                default -> throw new RuntimeException("This should not be possible, unknown type");
            };

            System.out.println("LIST text = %s  value = %s".formatted(text, value));
            return new EntityProperty<>(text, value);
        } else {
            var value = switch (ctx.type.getType()) {
                case QilletniLexer.ANY_TYPE -> visitQilletniTypedNode(ctx.expr(), AnyType.class); // TODO: Should this set AnyType.class?
                case QilletniLexer.INT_TYPE -> visitQilletniTypedNode(ctx.expr(), IntTypeImpl.class);
                case QilletniLexer.DOUBLE_TYPE -> visitQilletniTypedNode(ctx.expr(), DoubleTypeImpl.class);
                case QilletniLexer.BOOLEAN_TYPE -> visitQilletniTypedNode(ctx.expr(), BooleanTypeImpl.class);
                case QilletniLexer.STRING_TYPE -> visitQilletniTypedNode(ctx.expr(), StringTypeImpl.class);
                case QilletniLexer.COLLECTION_TYPE -> visitQilletniTypedNode(ctx.expr(), CollectionTypeImpl.class);
                case QilletniLexer.SONG_TYPE -> visitQilletniTypedNode(ctx.expr(), SongTypeImpl.class);
                case QilletniLexer.WEIGHTS_KEYWORD -> visitQilletniTypedNode(ctx.expr(), WeightsTypeImpl.class);
                case QilletniLexer.ALBUM_TYPE -> visitQilletniTypedNode(ctx.expr(), AlbumTypeImpl.class);
                case QilletniLexer.JAVA_TYPE -> visitQilletniTypedNode(ctx.expr(), JavaTypeImpl.class);
                case QilletniLexer.ID -> visitQilletniTypedNode(ctx.expr(), EntityTypeImpl.class);
                default -> throw new RuntimeException("This should not be possible, unknown type");
            };

            System.out.println("NOLIST text = %s  value = %s".formatted(text, value));
            return new EntityProperty<>(text, value);
        }
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
            return this.functionInvoker.<CollectionType>invokeFunction(ctx.function_call()).orElseThrow(FunctionDidntReturnException::new);
        }

        CollectionType collectionType;
        
        if (ctx.COLLECTION_TYPE() != null) {
            var songList = createListOfType(ctx.list_expression(), QilletniTypeClass.SONG);
            collectionType = new CollectionTypeImpl(songList.getItems().stream().map(SongType.class::cast).map(SongType::getTrack).toList());
        } else {
            var urlOrName = ctx.collection_url_or_name_pair();
            if (ctx.STRING() != null) {
                collectionType = new CollectionTypeImpl(StringUtility.removeQuotes(ctx.STRING().getText()));
            } else {
                collectionType = new CollectionTypeImpl(StringUtility.removeQuotes(urlOrName.STRING(0).getText()), StringUtility.removeQuotes(urlOrName.STRING(1).getText()));
            }
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

        return this.functionInvoker.<WeightsType>invokeFunction(ctx.function_call()).orElseThrow(FunctionDidntReturnException::new);
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
        } catch (Exception e) {
            if (e instanceof QilletniContextException qce) {
                if (!qce.isSourceSet() && ctx instanceof ParserRuleContext parserRuleContext) {
                    qce.setSource(parserRuleContext);
                }

                if (qce.getQilletniStackTrace() == null) {
                    qce.setQilletniStackTrace(qilletniStackTrace);
                }
                
                throw e;
            } else {
                QilletniContextException qce;
                
                if (ctx instanceof ParserRuleContext parserRuleContext) {
                    qce = new QilletniContextException(parserRuleContext, e);
                } else {
                    qce = new QilletniContextException(e);
                }

                qce.setQilletniStackTrace(qilletniStackTrace);
                
                throw qce;
            }
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

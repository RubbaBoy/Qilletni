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
import is.yarr.qilletni.lang.exceptions.InternalLanguageException;
import is.yarr.qilletni.lang.exceptions.InvalidConstructor;
import is.yarr.qilletni.lang.exceptions.InvalidStaticException;
import is.yarr.qilletni.lang.exceptions.InvalidSyntaxException;
import is.yarr.qilletni.lang.exceptions.ListOutOfBoundsException;
import is.yarr.qilletni.lang.exceptions.ListTransformerNotFoundException;
import is.yarr.qilletni.lang.exceptions.QilletniContextException;
import is.yarr.qilletni.lang.exceptions.TypeMismatchException;
import is.yarr.qilletni.lang.exceptions.VariableNotFoundException;
import is.yarr.qilletni.lang.internal.FunctionInvokerImpl;
import is.yarr.qilletni.lang.internal.NativeFunctionHandler;
import is.yarr.qilletni.lang.math.MixedExpression;
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
        // 1) Check for increments or post increments on an ID or ID[xx]
        //    Example: ++x, x++, x += 5, x[12]++, etc.
        if ((ctx.id_pre_crement != null) || (ctx.id_post_crement != null)
                || (ctx.id_post_crement_equals != null)) {
            if (ctx.LEFT_SBRACKET() != null) {
                return handleIncrementDecrementOnList(ctx);
            } else {
                return handleIncrementDecrement(ctx);
            }
        }

        // 2) Boolean literal:  true/false
        if (ctx.BOOL() != null) {
            boolean val = ctx.BOOL().getText().equals("true");
            return new BooleanTypeImpl(val);
        }

        // 3) Logical NOT:  ! expr
        if (ctx.NOT() != null) {
            // The child is expr(0)
            QilletniType sub = visitExpr(ctx.expr(0));
            if (!(sub instanceof BooleanType boolSub)) {
                throw new TypeMismatchException("NOT applied to non-boolean");
            }
            return new BooleanTypeImpl(!boolSub.getValue());
        }

        // 4) If it’s a relational op (>, <, >=, <=, ==, !=)
        if (ctx.REL_OP() != null) {
            return handleRelational(ctx);
        }

        if (ctx.ANDAND() != null || ctx.OROR() != null) {
            return handleLogicalAndOr(ctx);
        }

        // 5) Function call on nothing:  function_call()
        if (ctx.function_call() != null && ctx.expr().isEmpty()) {
            return functionInvoker
                    .invokeFunction(ctx.function_call(), /* optional: left side? */ null)
                    .orElseThrow(FunctionDidntReturnException::new);
        }

        // 6) Dot notation:  expr DOT function_call  or  expr DOT ID
        if (ctx.DOT() != null) {
            return handleDotAccess(ctx);
        }

        // 7) Parenthesized expression
        if (ctx.LEFT_PAREN() != null && ctx.expr().size() == 1) {
            return visitExpr(ctx.expr(0));
        }

        // 8) Entity initialize
        if (ctx.entity_initialize() != null) {
            return visitEntity_initialize(ctx.entity_initialize());
        }

        // 9) Standalone ID
        if (ctx.ID() != null) {
            return handleIDReference(ctx.ID().getText());
        }

        // 10) If we have an additive expression sub-rule
        if (ctx.addSubExpr() != null) {
            return visitAddSubExpr(ctx.addSubExpr());
        }

        // If everything else fails, fallback:
        return visitQilletniTypedNode(ctx.getChild(0), QilletniType.class);
    }

    private QilletniType handleIncrementDecrement(QilletniParser.ExprContext ctx) {
        // post/pre increment/decrement on an ID
        
        var currentScope = symbolTable.currentScope();
        var idText = LangParserUtils.requireNonNull(ctx.ID(), ctx, "Expected an ID for increment/decrement.").getText();
        BiFunction<Long, Long, Long> intOperation = (ctx.INCREMENT(0) != null || ctx.PLUS_EQUALS() != null) ? ((a, b) -> a + b) : ((a, b) -> a - b);
        BiFunction<Double, Double, Double> doubleOperation = (ctx.INCREMENT(0) != null || ctx.PLUS_EQUALS() != null) ? ((a, b) -> a + b) : ((a, b) -> a - b);

        var variableSymbol = currentScope.lookup(idText);
        var variable = variableSymbol.getValue();

        if (!variable.getTypeClass().equals(QilletniTypeClass.INT) && !variable.getTypeClass().equals(QilletniTypeClass.DOUBLE) && !variable.getTypeClass().equals(QilletniTypeClass.LIST)) {
            throw new TypeMismatchException("Cannot increment/decrement from a %s".formatted(variable.getTypeClass().getTypeName()));
        }

        switch (variable) {
            case IntType intVar -> {
                if (ctx.id_post_crement != null || ctx.id_post_crement_equals != null) {
                    var incrementBy = 1L;
                    if (ctx.id_post_crement_equals != null) {
                        incrementBy = visitQilletniTypedNode(ctx.expr(0), IntType.class).getValue();
                    }

                    var oldVal = intVar.getValue();
                    variableSymbol.setValue(new IntTypeImpl(intOperation.apply(oldVal, incrementBy)));
                    return new IntTypeImpl(oldVal);
                }

//              ctx.id_pre_crement != null
                var newVal = new IntTypeImpl(intOperation.apply(intVar.getValue(), 1L));
                variableSymbol.setValue(newVal);
                return newVal;
            }
            case DoubleType doubleVar -> {

                if (ctx.id_post_crement != null || ctx.id_post_crement_equals != null) {
                    var incrementBy = 1D;
                    if (ctx.id_post_crement_equals != null) {
                        // TODO: Convert to double if int?
                        incrementBy = visitQilletniTypedNode(ctx.expr(0), DoubleType.class).getValue();
                    }

                    var oldVal = doubleVar.getValue();
                    variableSymbol.setValue(new DoubleTypeImpl(doubleOperation.apply(oldVal, incrementBy)));
                    return new DoubleTypeImpl(oldVal);
                }

//              ctx.id_pre_crement != null
                var newVal = new DoubleTypeImpl(doubleOperation.apply(doubleVar.getValue(), 1D));
                variableSymbol.setValue(newVal);
                return newVal;
            }
            case ListType listVar when ctx.PLUS_EQUALS() != null -> {
                var rightVar = visitQilletniTypedNode(ctx.expr(0), ListType.class);
                var newList = addListsToNewList(listVar, rightVar, ctx);
                variableSymbol.setValue(newList);
                return newList;
            }
            default ->
                    throw new InternalLanguageException(ctx, "Increment/decrement only supported for int and double types.");
        }
    }

    private QilletniType handleIncrementDecrementOnList(QilletniParser.ExprContext ctx) {
        // post/pre increment/decrement on a list accessor ( eg.  ID[expr]++ )
        
        var currentScope = symbolTable.currentScope();
        var idText = LangParserUtils.requireNonNull(ctx.ID(), ctx, "Expected an ID for increment/decrement.").getText();
        BiFunction<Long, Long, Long> intOperation = (ctx.INCREMENT(0) != null || ctx.PLUS_EQUALS() != null) ? ((a, b) -> a + b) : ((a, b) -> a - b);
        BiFunction<Double, Double, Double> doubleOperation = (ctx.INCREMENT(0) != null || ctx.PLUS_EQUALS() != null) ? ((a, b) -> a + b) : ((a, b) -> a - b);

        // Access the list and index
        var list = currentScope.<ListTypeImpl>lookup(idText).getValue();
        var index = (int) visitQilletniTypedNode(ctx.expr(0), IntTypeImpl.class).getValue();

        if (index < 0 || index > list.getItems().size()) {
            throw new ListOutOfBoundsException(ctx, "Attempted to access index %d on a list with a size of %d".formatted(index, list.getItems().size()));
        }
        
        if (!list.getSubType().equals(QilletniTypeClass.INT) && !list.getSubType().equals(QilletniTypeClass.DOUBLE)) {
            throw new TypeMismatchException("Cannot increment/decrement from a %s[]".formatted(list.getSubType().getTypeName()));
        }

        // Increment, similarly to how handleIncrementDecrement does it
        if (list.getItems().get(index) instanceof IntType itemIntVar) {
            if (ctx.id_post_crement != null || ctx.id_post_crement_equals != null) {
                var incrementBy = 1L;
                if (ctx.id_post_crement_equals != null) {
                    incrementBy = visitQilletniTypedNode(ctx.expr(1), IntType.class).getValue();
                }

                var oldVal = itemIntVar.getValue();
                list.getItems().set(index, new IntTypeImpl(intOperation.apply(oldVal, incrementBy)));
                return new IntTypeImpl(oldVal);
            }

//          ctx.id_pre_crement != null
            var newItem = new IntTypeImpl(intOperation.apply(itemIntVar.getValue(), 1L));
            list.getItems().set(index, newItem);
            return newItem;
        } else if (list.getItems().get(index) instanceof DoubleType itemDoubleVar) {
            if (ctx.id_post_crement != null || ctx.id_post_crement_equals != null) {
                var incrementBy = 1D;
                if (ctx.id_post_crement_equals != null) {
                    incrementBy = visitQilletniTypedNode(ctx.expr(1), DoubleType.class).getValue();
                }

                var oldVal = itemDoubleVar.getValue();
                list.getItems().set(index, new DoubleTypeImpl(doubleOperation.apply(oldVal, incrementBy)));
                return new DoubleTypeImpl(oldVal);
            }

//          ctx.id_pre_crement != null
            var newItem = new DoubleTypeImpl(doubleOperation.apply(itemDoubleVar.getValue(), 1D));
            list.getItems().set(index, newItem);
            return newItem;
        } else {
            throw new InternalLanguageException(ctx, "Increment/decrement only supported for int and double types. This should never occur, please report this.");
        }
    }

    private QilletniType handleRelational(QilletniParser.ExprContext ctx) {
        var leftChild = ctx.expr(0);
        var rightChild = ctx.expr(1);
        var leftVal = visitQilletniTypedNode(leftChild);
        var rightVal = visitQilletniTypedNode(rightChild);

        var relOp = ctx.REL_OP().getText(); // e.g. ">", "<", "==", "!="

        if ("!==".contains(relOp)) {
            var areEqual = leftVal.qilletniEquals(rightVal);

            if (relOp.equals("!=")) {
                areEqual = !areEqual;
            }

            return new BooleanTypeImpl(areEqual);
        } else {
            double left;
            double right;

            if (leftVal instanceof IntType intType) {
                left = intType.getValue();
            } else if (leftVal instanceof DoubleType doubleType) {
                left = doubleType.getValue();
            } else {
                throw new TypeMismatchException(leftChild, "Can only compare number types! Left param is a %s".formatted(leftVal.typeName()));
            }

            if (rightVal instanceof IntType intType) {
                right = intType.getValue();
            } else if (rightVal instanceof DoubleType doubleType) {
                right = doubleType.getValue();
            } else {
                throw new TypeMismatchException(rightChild, "Can only compare number types! Right param is a %s".formatted(rightVal.typeName()));
            }

            LOGGER.debug("Comparing {} {} {}", left, relOp, right);

            var comparisonResult = switch (relOp) {
                case ">" -> left > right;
                case "<" -> left < right;
                case "<=" -> left <= right;
                case ">=" -> left >= right;
                default -> throw new IllegalStateException("Unexpected value: %s".formatted(relOp));
            };

            return new BooleanTypeImpl(comparisonResult);
        }
    }

    private QilletniType handleDotAccess(QilletniParser.ExprContext ctx) {
        var leftExpr = visitQilletniTypedNode(ctx.expr(0));
        
        // invokeFunction() doesn't need a scope passed to it, so it may handle it directly before one is provided 
        if (ctx.function_call() != null) {
            // This throws because it's an expression, so it expects a return value
            return functionInvoker.invokeFunction(ctx.function_call(), leftExpr)
                    .orElseThrow(FunctionDidntReturnException::new);
        }

        // Variable access
        
        var scope = switch (leftExpr) {
            case EntityType entityType -> entityType.getEntityScope();
            case ImportAliasType importAliasType -> importAliasType.getScope();
            default -> throw new TypeMismatchException("Can only access property of entity or import alias");
        };
        
        var idText = LangParserUtils.requireNonNull(ctx.ID(), ctx, "Expected an ID for dot access.").getText();
        BiFunction<Long, Long, Long> intOperation = (ctx.INCREMENT(0) != null || ctx.PLUS_EQUALS() != null) ? ((a, b) -> a + b) : ((a, b) -> a - b);
        BiFunction<Double, Double, Double> doubleOperation = (ctx.INCREMENT(0) != null || ctx.PLUS_EQUALS() != null) ? ((a, b) -> a + b) : ((a, b) -> a - b);

        if (idText.startsWith("_")) {
            throw new VariableNotFoundException(ctx, "Cannot access private variable");
        }

        var propertySymbol = scope.lookup(idText);
        var property = propertySymbol.getValue();

        if (ctx.post_crement != null || ctx.post_crement_equals != null) {
            if (!property.getTypeClass().equals(QilletniTypeClass.INT) && !property.getTypeClass().equals(QilletniTypeClass.DOUBLE)) {
                throw new TypeMismatchException("Cannot increment/decrement from a %s".formatted(property.getTypeClass().getTypeName()));
            }

            switch (property) {
                case IntType intVar -> {
                    var incrementBy = 1L;
                    if (ctx.id_post_crement_equals != null) {
                        incrementBy = visitQilletniTypedNode(ctx.expr(1), IntType.class).getValue();
                    }

                    var oldVal = intVar.getValue();
                    propertySymbol.setValue(new IntTypeImpl(intOperation.apply(oldVal, incrementBy)));
                    return new IntTypeImpl(oldVal);
                }
                case DoubleType doubleVar -> {
                    var incrementBy = 1D;
                    if (ctx.id_post_crement_equals != null) {
                        // TODO: Convert to double if int?
                        incrementBy = visitQilletniTypedNode(ctx.expr(1), DoubleType.class).getValue();
                    }

                    var oldVal = doubleVar.getValue();
                    propertySymbol.setValue(new DoubleTypeImpl(doubleOperation.apply(oldVal, incrementBy)));
                    return new DoubleTypeImpl(oldVal);
                }
                case ListType listVar when ctx.PLUS_EQUALS() != null -> {
                    var rightVar = visitQilletniTypedNode(ctx.expr(1), ListType.class);
                    var newList = addListsToNewList(listVar, rightVar, ctx);
                    propertySymbol.setValue(newList);
                    return newList;
                }
                default -> 
                        throw new InternalLanguageException(ctx, "Increment/decrement only supported for int and double types. %s".formatted(ctx.PLUS_EQUALS() != null ? "true" : "false"));
            }
        }

        return property;
    }
    
    private BooleanType handleLogicalAndOr(QilletniParser.ExprContext ctx) {
        var operator = ctx.ANDAND() != null ? "&&" : "||";
        
        var leftVal = visitQilletniTypedNode(ctx.expr(0));
        if (!(leftVal instanceof BooleanType leftBool)) {
            throw new InternalLanguageException(ctx, "Expected boolean on left side for %s, got %s".formatted(operator, leftVal.typeName()));
        }
        
        if (operator.equals("&&") && !leftBool.getValue()) {
            return BooleanTypeImpl.FALSE;
        }
        
        if (operator.equals("||") && leftBool.getValue()) {
            return BooleanTypeImpl.TRUE;
        }
        
        var rightVal = visitQilletniTypedNode(ctx.expr(1));
        if (!(rightVal instanceof BooleanType rightBool)) {
            throw new InternalLanguageException(ctx, "Expected boolean on right side for %s, got %s".formatted(operator, leftVal.typeName()));
        }
        
        return new BooleanTypeImpl(rightBool.getValue());
    }
    
    private ListType addListsToNewList(ListType left, ListType right, ParserRuleContext ctx) {
        if (!left.getSubType().equals(right.getSubType())) {
            throw new TypeMismatchException(ctx, "Cannot add lists of mismatched types: %s and %s".formatted(left.getSubType().getTypeName(), right.getSubType().getTypeName()));
        }

        var leftItems = left.getItems();
        var rightItems = right.getItems();

        var newItems = new ArrayList<>(leftItems);
        newItems.addAll(rightItems);

        return new ListTypeImpl(left.getSubType(), newItems);
    }

    @Override
    public QilletniType visitAddSubExpr(QilletniParser.AddSubExprContext ctx) {
        // Start with the first multiplicative expression
        var result = visitMulDivExpr(ctx.mulDivExpr(0));

        // Then fold left-to-right for each (+ or -) and the subsequent mulDivExpr
        for (int i = 1; i < ctx.mulDivExpr().size(); i++) {
            // The operator token is between the two mulDivExpr sub-nodes
            var opText = ctx.getChild(2 * i - 1).getText(); // could be "+" or "-"

            var rightVal = visitMulDivExpr(ctx.mulDivExpr(i));

            if (opText.equals("+")) {
                result = handleAddition(result, rightVal, ctx);
            } else if (opText.equals("-")) {
                result = handleSubtraction(result, rightVal, ctx);
            } else {
                throw new InternalLanguageException(ctx, "Expected operator to be +/-. Got: %s.".formatted(opText));
            }
        }
        return result;
    }

    private QilletniType handleAddition(QilletniType leftType, QilletniType rightType, QilletniParser.AddSubExprContext ctx) {
        return switch (new MixedExpression<>(leftType, rightType)) {
            case MixedExpression(IntType left, IntType right) -> new IntTypeImpl(left.getValue() + right.getValue());
            case MixedExpression(DoubleType left, DoubleType right) -> new DoubleTypeImpl(left.getValue() + right.getValue());
            case MixedExpression(DoubleType left, IntType right) -> new DoubleTypeImpl(left.getValue() + right.getValue());
            case MixedExpression(IntType left, DoubleType right) -> new DoubleTypeImpl(left.getValue() + right.getValue());
            
            // Concatenate lists together
            case MixedExpression(ListType left, ListType right) -> addListsToNewList(left, right, ctx);

            // TODO: Custom addition? Maybe other operators too?
            // Concatenate anything with a string
            case MixedExpression(StringType left, QilletniType right) -> new StringTypeImpl(left.stringValue() + right.stringValue());
            case MixedExpression(QilletniType left, StringType right) -> new StringTypeImpl(left.stringValue() + right.stringValue());
            default -> throw new TypeMismatchException(ctx, "Can only add numbers, strings, or things to strings. Got (%s + %s)".formatted(leftType.typeName(), rightType.typeName()));
        };
    }

    private QilletniType handleSubtraction(QilletniType leftType, QilletniType rightType, QilletniParser.AddSubExprContext ctx) {
        return switch (new MixedExpression<>(leftType, rightType)) {
            case MixedExpression(IntType left, IntType right) -> new IntTypeImpl(left.getValue() - right.getValue());
            case MixedExpression(DoubleType left, DoubleType right) -> new DoubleTypeImpl(left.getValue() - right.getValue());
            case MixedExpression(DoubleType left, IntType right) -> new DoubleTypeImpl(left.getValue() - right.getValue());
            case MixedExpression(IntType left, DoubleType right) -> new DoubleTypeImpl(left.getValue() - right.getValue());
            default -> throw new InternalLanguageException(ctx, "Expected numbers for subtraction, got (%s - %s)".formatted(leftType.typeName(), rightType.typeName()));
        };
    }

    @Override
    public QilletniType visitMulDivExpr(QilletniParser.MulDivExprContext ctx) {
        // Evaluate the first unaryArithmetic
        QilletniType result = visitUnaryArithmetic(ctx.unaryArithmetic(0));

        // Then fold each operator and unaryArithmetic
        for (int i = 1; i < ctx.unaryArithmetic().size(); i++) {
            String opText = ctx.getChild(2 * i - 1).getText(); // "*", "/", "/~", or "%"

            QilletniType rightVal = visitUnaryArithmetic(ctx.unaryArithmetic(i));

            result = switch (opText) {
                case "*" -> handleMultiplication(result, rightVal, ctx);
                case "/" -> handleFloatingDivision(result, rightVal, ctx);
                case "/~" -> handleFloorDivision(result, rightVal, ctx);
                case "%" -> handleModulo(result, rightVal, ctx);
                default -> throw new InternalLanguageException(ctx, "Unexpected operator: %s".formatted(opText));
            };
        }
        return result;
    }

    private QilletniType handleMultiplication(QilletniType leftType, QilletniType rightType, QilletniParser.MulDivExprContext ctx) {
        return switch (new MixedExpression<>(leftType, rightType)) {
            case MixedExpression(IntType left, IntType right) -> new IntTypeImpl(left.getValue() * right.getValue());
            case MixedExpression(DoubleType left, DoubleType right) -> new DoubleTypeImpl(left.getValue() * right.getValue());
            case MixedExpression(DoubleType left, IntType right) -> new DoubleTypeImpl(left.getValue() * right.getValue());
            case MixedExpression(IntType left, DoubleType right) -> new DoubleTypeImpl(left.getValue() * right.getValue());
            default -> throw new InternalLanguageException(ctx, "Expected numbers for multiplication, got (%s - %s)".formatted(leftType.typeName(), rightType.typeName()));
        };
    }

    private QilletniType handleFloatingDivision(QilletniType leftType, QilletniType rightType, QilletniParser.MulDivExprContext ctx) {
        return switch (new MixedExpression<>(leftType, rightType)) {
            case MixedExpression(IntType left, IntType right) -> new DoubleTypeImpl((double) left.getValue() / right.getValue());
            case MixedExpression(DoubleType left, DoubleType right) -> new DoubleTypeImpl(left.getValue() / right.getValue());
            case MixedExpression(DoubleType left, IntType right) -> new DoubleTypeImpl(left.getValue() / right.getValue());
            case MixedExpression(IntType left, DoubleType right) -> new DoubleTypeImpl(left.getValue() / right.getValue());
            default -> throw new InternalLanguageException(ctx, "Expected numbers for division, got (%s - %s)".formatted(leftType.typeName(), rightType.typeName()));
        };
    }

    private QilletniType handleFloorDivision(QilletniType leftType, QilletniType rightType, QilletniParser.MulDivExprContext ctx) {
        return switch (new MixedExpression<>(leftType, rightType)) {
            case MixedExpression(IntType left, IntType right) -> new IntTypeImpl(left.getValue() / right.getValue());
            case MixedExpression(DoubleType left, DoubleType right) -> new IntTypeImpl((int) (left.getValue() / right.getValue()));
            case MixedExpression(DoubleType left, IntType right) -> new IntTypeImpl((int) (left.getValue() / right.getValue()));
            case MixedExpression(IntType left, DoubleType right) -> new IntTypeImpl((long) (left.getValue() / right.getValue()));
            default -> throw new InternalLanguageException(ctx, "Expected numbers for division, got (%s - %s)".formatted(leftType.typeName(), rightType.typeName()));
        };
    }

    private QilletniType handleModulo(QilletniType leftType, QilletniType rightType, QilletniParser.MulDivExprContext ctx) {
        return switch (new MixedExpression<>(leftType, rightType)) {
            case MixedExpression(IntType left, IntType right) -> new IntTypeImpl(left.getValue() % right.getValue());
            case MixedExpression(DoubleType left, DoubleType right) -> new DoubleTypeImpl(left.getValue() % right.getValue());
            case MixedExpression(DoubleType left, IntType right) -> new DoubleTypeImpl(left.getValue() % right.getValue());
            case MixedExpression(IntType left, DoubleType right) -> new DoubleTypeImpl(left.getValue() % right.getValue());
            default -> throw new InternalLanguageException(ctx, "Expected numbers for modulo, got (%s - %s)".formatted(leftType.typeName(), rightType.typeName()));
        };
    }

    @Override
    public QilletniType visitUnaryArithmetic(QilletniParser.UnaryArithmeticContext ctx) {
        return visitPrimaryArithmetic(ctx.primaryArithmetic());
    }

    @Override
    public QilletniType visitPrimaryArithmetic(QilletniParser.PrimaryArithmeticContext ctx) {
        if (ctx.LEFT_PAREN() != null) {
            return visitQilletniTypedNode(ctx.expr());
        }
        
        if (ctx.function_call() != null) {
            return functionInvoker.invokeFunction(ctx.function_call())
                    .orElseThrow(FunctionDidntReturnException::new);
        }

        if (ctx.int_expr() != null) {
            return visitInt_expr(ctx.int_expr());
        }
        
        if (ctx.double_expr() != null) {
            return visitDouble_expr(ctx.double_expr());
        }
        
        if (ctx.str_expr() != null) {
            return visitStr_expr(ctx.str_expr());
        }
        
        if (ctx.list_expression() != null) {
            return visitList_expression(ctx.list_expression());
        }
        
        if (ctx.ID() != null) {
            return handleIDReference(ctx.ID().getText());
        }

        throw new RuntimeException("Unknown primaryArithmetic structure");
    }

    private QilletniType handleIDReference(String id) {
        // Static access for entities
        if (entityDefinitionManager.isDefined(id)) {
            return entityDefinitionManager.lookup(id).createStaticInstance();
        }
        
        // Normal variable address
        var currentScope = symbolTable.currentScope();
        var variableSymbol = currentScope.lookup(id);
        return variableSymbol.getValue();
    }


    @Override
    public StringType visitStr_expr(QilletniParser.Str_exprContext ctx) {
        // String cast:  string( expr )
        if (ctx.STRING_TYPE() != null) {
            var exprValue = visitQilletniTypedNode(ctx.expr());
            return new StringTypeImpl(exprValue.stringValue());
        }
        
        // String literal:  "..."
        var stringLiteral = ctx.STRING().getText();
        return new StringTypeImpl(stringLiteral.substring(1, stringLiteral.length() - 1).translateEscapes());
    }

    @Override
    public IntType visitInt_expr(QilletniParser.Int_exprContext ctx) {
        // Int cast:  int( expr )
        if (ctx.INT_TYPE() != null) {
            var exprValue = visitQilletniTypedNode(ctx.expr());
            
            // If it's already an int, return it
            if (exprValue instanceof IntType intType) {
                return intType;
            } else if (exprValue instanceof DoubleType doubleType) {
                return new IntTypeImpl((long) Math.floor(doubleType.getValue()));
            }

            throw new TypeMismatchException(ctx, "Expected a number to cast to int, got %s".formatted(exprValue.typeName()));
        }

        // Int literal:  123...
        return new IntTypeImpl(Integer.parseInt(ctx.INT().getText()));
    }

    @Override
    public DoubleType visitDouble_expr(QilletniParser.Double_exprContext ctx) {
        // String cast:  string( expr )
        if (ctx.DOUBLE_TYPE() != null) {
            var exprValue = visitQilletniTypedNode(ctx.expr());
            
            // If it's already a double, return it
            if (exprValue instanceof DoubleType doubleType) {
                return doubleType;
            } else if (exprValue instanceof IntType intType) {
                return new DoubleTypeImpl((double) intType.getValue());
            }
            
            throw new TypeMismatchException(ctx, "Expected a number to cast to double, got %s".formatted(exprValue.typeName()));
        }

        // Double literal:  "12.34..."
        return new DoubleTypeImpl(Double.parseDouble(ctx.DOUBLE().getText()));
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
        
        // Not an entity
        if (type.equals(QilletniTypeClass.LIST)) {
            if (ctx.type == null) {
                throw new InvalidSyntaxException(ctx, "Required type for list type check");
            }
            
            var subType = TypeUtils.getTypeFromStringOrEntity(ctx.type.getText());

            if (!(checkingVariable instanceof ListType listType)) {
                return BooleanTypeImpl.FALSE;
            }
            
            var eq = listType.getSubType().equals(subType);
            return new BooleanTypeImpl(eq);
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
            if (typeList.size() == 1) {
                listType = typeList.getFirst();
            }

            return new ListTypeImpl(listType, items);
        });
    }
    
    private <T extends QilletniType> ListType createListOfType(QilletniParser.List_expressionContext ctx, QilletniTypeClass<T> listType) {
        return checkEmptyTypedList(ctx, listType).or(() -> checkAlreadyComputedList(ctx)).orElseGet(() -> {
            var items = this.<List<QilletniType>>visitNode(ctx.expr_list());
            
            // Check if all items in the list are either of the list type, or may be transformed
            // This is to reduce load and waiting for an exception to occur during transformation
            for (var listItem : items) {
                if (!listType.isAssignableFrom(listItem.getTypeClass()) && !listTypeTransformer.doesTransformerExist(listType, listItem.getTypeClass())) {
                    throw new ListTransformerNotFoundException(String.format("No list transformer found from %s to %s", listItem.getTypeClass().getTypeName(), listType.getTypeName()));
                }
            }

            var transformedItems = items.stream().map(listItem -> {
                if (listType.isAssignableFrom(listItem.getTypeClass())) { // This means if listType is ANY, no transformations happen
                    return listItem;
                }

                // This should never error out due to the above loop/check
                return listTypeTransformer.transformType(listType, listItem);
            }).toList();

            return new ListTypeImpl(listType, transformedItems);
        });
    }

    /**
     * Creates a {@link ListTypeImpl} of the type {@code listType} from an {@link ParserRuleContext} that is assumed to have
     * one child of {@link QilletniParser.List_expressionContext}.
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
            
            // If a list is empty and has no type assigned to it, treat it as a list of the type specified
            // NOTE: This lets  `song[] s = any[]` work?
            if (list.getItems().isEmpty() && list.getSubType().equals(QilletniTypeClass.ANY)) {
                return new ListTypeImpl(listType, Collections.emptyList());
            }
            
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
        // TODO: Maybe add a way to execute Java code via JShell?

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

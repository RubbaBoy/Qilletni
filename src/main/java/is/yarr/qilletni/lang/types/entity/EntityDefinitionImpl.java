package is.yarr.qilletni.lang.types.entity;

import is.yarr.qilletni.api.CollectionUtility;
import is.yarr.qilletni.api.CollectionUtility.Entry;
import is.yarr.qilletni.api.lang.internal.FunctionInvoker;
import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.StaticEntityType;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinition;
import is.yarr.qilletni.api.lang.types.entity.UninitializedType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.lang.exceptions.TypeMismatchException;
import is.yarr.qilletni.lang.table.ScopeImpl;
import is.yarr.qilletni.lang.table.SymbolImpl;
import is.yarr.qilletni.lang.types.EntityTypeImpl;
import is.yarr.qilletni.lang.types.StaticEntityTypeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Effectively the "Type" of a {@link EntityType}.
 */
public class EntityDefinitionImpl implements EntityDefinition {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityDefinitionImpl.class);

    private final FunctionInvoker functionInvoker;
    private final String typeName;

    // properties NOT in constructor MUST be constant ( <-- not yet implemented)
    /**
     * Initial defined properties. A supplier is used to allow for lazy initialization, and individual values per entity.
     */
    private final Map<String, Supplier<QilletniType>> properties;

    /**
     * Initial undefined properties, which MUST appear in constructor
     */
    private final Map<String, UninitializedType> uninitializedParams;

    /**
     * Consumes the entity's scope and populates it with a function each, that belong to the entity instance.
     */
    private final List<FunctionPopulator> entityFunctionPopulators;
    private final Scope parentScope;
    
    private final QilletniTypeClass<EntityType> qilletniTypeClass;

    public EntityDefinitionImpl(FunctionInvoker functionInvoker, String typeName, Map<String, Supplier<QilletniType>> properties, Map<String, UninitializedType> uninitializedParams, List<FunctionPopulator> entityFunctionPopulators, Scope parentScope) {
        this.functionInvoker = functionInvoker;
        this.typeName = typeName;
        this.qilletniTypeClass = new QilletniTypeClass<>(this, typeName);
        this.properties = properties;
        this.uninitializedParams = uninitializedParams;
        this.entityFunctionPopulators = entityFunctionPopulators;
        this.parentScope = parentScope;
    }

    @Override
    public EntityType createInstance(List<QilletniType> constructorParams) {
        return new EntityTypeImpl(createScope(constructorParams), this, functionInvoker);
    }

    @Override
    public EntityType createInstance(QilletniType... constructorParams) {
        return createInstance(List.of(constructorParams));
    }

    @Override
    public StaticEntityType createStaticInstance() {
        var scope = new ScopeImpl(parentScope, Scope.ScopeType.ENTITY, "static %s".formatted(typeName), qilletniTypeClass);
        
        entityFunctionPopulators.stream()
                .filter(FunctionPopulator::isStaticFunction)
                .forEach(populator -> populator.functionPopulator().accept(scope));
        
        return new StaticEntityTypeImpl(scope, this);
    }

    protected Scope createScope(List<QilletniType> constructorParams) {
        var scope = new ScopeImpl(parentScope, Scope.ScopeType.ENTITY, "entity %s".formatted(typeName), qilletniTypeClass);

        if (uninitializedParams.size() != constructorParams.size()) {
            throw new InvalidParameterException("Invalid constructor invocation");
        }

        for (var entry : CollectionUtility.getRecordEntries(properties)) {
            var name = entry.k();
            var qilletniType = entry.v().get();
            scope.define(SymbolImpl.createGenericSymbol(name, qilletniType.getTypeClass(), qilletniType));
        }

        int index = 0;
        for (var entry : CollectionUtility.getRecordEntries(uninitializedParams)) {
            var name = entry.k();
            var uninitializedType = entry.v();
            var currentParam = constructorParams.get(index);
            
            
            if (!QilletniTypeClass.ANY.equals(uninitializedType.getNativeTypeClass())) {
                if (uninitializedType.isEntity()) {
                    if (!(currentParam instanceof EntityType entityType)) {
                        throw new TypeMismatchException("Expected a %s but received a %s in parameter %d of %s constructor".formatted(uninitializedType.getTypeName(), currentParam.typeName(), index + 1, typeName));
                    }

                    if (!entityType.getEntityDefinition().equals(uninitializedType.getEntityDefinition())) {
                        throw new TypeMismatchException("Expected a %s but received a %s in parameter %d of %s constructor".formatted(uninitializedType.getTypeName(), currentParam.typeName(), index + 1, typeName));
                    }

                    // valid entity to set
                }

                if (!uninitializedType.isEntity()) {
                    if (currentParam instanceof EntityType) {
                        throw new TypeMismatchException("Expected a %s but received a %s in parameter %d of %s constructor".formatted(uninitializedType.getTypeName(), currentParam.typeName(), index + 1, typeName));
                    }

                    if (!uninitializedType.getNativeTypeClass().isAssignableFrom(currentParam.getTypeClass())) {
                        throw new TypeMismatchException("Expected a %s but received a %s in parameter %d of %s constructor".formatted(uninitializedType.getTypeName(), currentParam.typeName(), index + 1, typeName));
                    }

                    // valid QilletniType
                }
            }
            
            LOGGER.debug("Setting constructor param {} = {}", name, currentParam);
            scope.define(SymbolImpl.createGenericSymbol(name, currentParam.getTypeClass(), currentParam));

            index++;
        }

        entityFunctionPopulators.forEach(populator -> populator.functionPopulator().accept(scope));

        return scope;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public Map<String, UninitializedType> getUninitializedParams() {
        return uninitializedParams;
    }

    @Override
    public QilletniTypeClass<EntityType> getQilletniTypeClass() {
        return qilletniTypeClass;
    }
}

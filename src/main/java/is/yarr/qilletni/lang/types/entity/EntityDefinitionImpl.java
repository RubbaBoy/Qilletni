package is.yarr.qilletni.lang.types.entity;

import is.yarr.qilletni.api.CollectionUtility;
import is.yarr.qilletni.api.CollectionUtility.Entry;
import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinition;
import is.yarr.qilletni.api.lang.types.entity.UninitializedType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.lang.exceptions.TypeMismatchException;
import is.yarr.qilletni.lang.table.ScopeImpl;
import is.yarr.qilletni.lang.table.SymbolImpl;
import is.yarr.qilletni.lang.types.EntityTypeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Effectively the "Type" of a {@link EntityType}.
 */
public class EntityDefinitionImpl implements EntityDefinition {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityDefinitionImpl.class);

    private final String typeName;

    // properties NOT in constructor MUST be constant ( <-- not yet implemented)
    /**
     * Initial defined properties
     */
    private final Map<String, QilletniType> properties;

    /**
     * Initial undefined properties, which MUST appear in constructor
     */
    private final Map<String, UninitializedType> uninitializedParams;

    /**
     * Consumes the entity's scope and populates it with a function each, that belong to the entity instance.
     */
    private final List<Consumer<Scope>> entityFunctionPopulators;
    private final Scope parentScope;
    
    private final QilletniTypeClass<EntityType> qilletniTypeClass;

    public EntityDefinitionImpl(String typeName, Map<String, QilletniType> properties, Map<String, UninitializedType> uninitializedParams, List<Consumer<Scope>> entityFunctionPopulators, Scope parentScope) {
        this.typeName = typeName;
        this.qilletniTypeClass = new QilletniTypeClass<>(this, typeName);
        this.properties = properties;
        this.uninitializedParams = uninitializedParams;
        this.entityFunctionPopulators = entityFunctionPopulators;
        this.parentScope = parentScope;
    }

    @Override
    public EntityType createInstance(List<QilletniType> constructorParams) {
        return new EntityTypeImpl(createScope(constructorParams), this);
    }

    @Override
    public EntityType createInstance(QilletniType... constructorParams) {
        return createInstance(List.of(constructorParams));
    }

    protected Scope createScope(List<QilletniType> constructorParams) {
        var scope = new ScopeImpl(parentScope, Scope.ScopeType.ENTITY, "entity");

        if (uninitializedParams.size() != constructorParams.size()) {
            throw new InvalidParameterException("Invalid constructor invocation");
        }

        for (var entry : CollectionUtility.getRecordEntries(properties)) {
            var name = entry.k();
            var qilletniType = entry.v();
            scope.define(SymbolImpl.createGenericSymbol(name, qilletniType.getTypeClass(), qilletniType));
        }

        int index = 0;
        for (var entry : CollectionUtility.getRecordEntries(uninitializedParams)) {
            var name = entry.k();
            var uninitializedType = entry.v();
            var currentParam = constructorParams.get(index);

            if (uninitializedType.isEntity()) {
                if (!(currentParam instanceof EntityType entityType)) {
                    throw new TypeMismatchException("Expected a " + uninitializedType.getTypeName() + " but received a " + currentParam.typeName() + " in parameter " + (index + 1));
                }
                
                if (!entityType.getEntityDefinition().equals(uninitializedType.getEntityDefinition())) {
                    throw new TypeMismatchException("Expected a " + uninitializedType.getTypeName() + " but received a " + currentParam.typeName() + " in parameter " + (index + 1));
                }
                
                // valid entity to set
            }

            if (!uninitializedType.isEntity()) {
                if (currentParam instanceof EntityType) {
                    throw new TypeMismatchException("Expected a " + uninitializedType.getTypeName() + " but received a " + currentParam.typeName() + " in parameter " + (index + 1));
                }
                
                if (!uninitializedType.getNativeTypeClass().equals(currentParam.getTypeClass())) {
                    throw new TypeMismatchException("Expected a " + uninitializedType.getTypeName() + " but received a " + currentParam.typeName() + " in parameter " + (index + 1));
                }
                
                // valid QilletniType
            }
            
            LOGGER.debug("Setting constructor param {} = {}", name, currentParam);
            scope.define(SymbolImpl.createGenericSymbol(name, currentParam.getTypeClass(), currentParam));

            index++;
        }

        entityFunctionPopulators.forEach(consumer -> consumer.accept(scope));

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

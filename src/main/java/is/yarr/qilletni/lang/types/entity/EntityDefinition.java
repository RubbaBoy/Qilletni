package is.yarr.qilletni.lang.types.entity;

import is.yarr.qilletni.MapUtility;
import is.yarr.qilletni.MapUtility.Entry;
import is.yarr.qilletni.lang.exceptions.InvalidSyntaxException;
import is.yarr.qilletni.lang.exceptions.TypeMismatchException;
import is.yarr.qilletni.lang.table.Scope;
import is.yarr.qilletni.lang.table.Symbol;
import is.yarr.qilletni.lang.types.EntityType;
import is.yarr.qilletni.lang.types.QilletniType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Effectively the "Type" of a {@link is.yarr.qilletni.lang.types.EntityType}.
 */
public class EntityDefinition {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityDefinition.class);

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
    private final Scope globalScope;

    public EntityDefinition(String typeName, Map<String, QilletniType> properties, Map<String, UninitializedType> uninitializedParams, List<Consumer<Scope>> entityFunctionPopulators, Scope globalScope) {
        this.typeName = typeName;
        this.properties = properties;
        this.uninitializedParams = uninitializedParams;
        this.entityFunctionPopulators = entityFunctionPopulators;
        this.globalScope = globalScope;
    }

    public EntityType createInstance(List<QilletniType> constructorParams) {
        return new EntityType(createScope(constructorParams), this);
    }

    private Scope createScope(List<QilletniType> constructorParams) {
        var scope = new Scope(globalScope);

        if (uninitializedParams.size() != constructorParams.size()) {
            throw new InvalidSyntaxException("Invalid constructor invocation");
        }

        for (Entry(var name, var qilletniType) : MapUtility.getRecordEntries(properties)) {
            scope.define(new Symbol<>(name, Symbol.SymbolType.fromQilletniType(qilletniType.getClass()), qilletniType));
        }

        int index = 0;
        for (Entry(var name, var uninitializedType) : MapUtility.getRecordEntries(uninitializedParams)) {
            var currentParam = constructorParams.get(index);

            if (!uninitializedType.isNative()) {
                if (!(currentParam instanceof EntityType entityType)) {
                    throw new TypeMismatchException("Expected a " + uninitializedType.getTypeName() + " but received a " + currentParam.typeName());
                }
                
                if (!entityType.getEntityDefinition().equals(uninitializedType.getEntityDefinition())) {
                    throw new TypeMismatchException("Expected a " + uninitializedType.getTypeName() + " but received a " + currentParam.typeName());
                }
                
                // valid entity to set
            }

            if (uninitializedType.isNative()) {
                if (currentParam instanceof EntityType) {
                    throw new TypeMismatchException("Expected a " + uninitializedType.getTypeName() + " but received a " + currentParam.typeName());
                }
                
                if (!uninitializedType.getNativeTypeClass().equals(currentParam.getClass())) {
                    throw new TypeMismatchException("Expected a " + uninitializedType.getTypeName() + " but received a " + currentParam.typeName());
                }
                
                // valid QilletniType
            }
            
            LOGGER.debug("Setting constructor param {} = {}", name, currentParam);
            scope.define(new Symbol<>(name, Symbol.SymbolType.fromQilletniType(currentParam.getClass()), currentParam));

            index++;
        }

        entityFunctionPopulators.forEach(consumer -> consumer.accept(scope));

        return scope;
    }

    public String getTypeName() {
        return typeName;
    }

    public Map<String, UninitializedType> getUninitializedParams() {
        return uninitializedParams;
    }
}

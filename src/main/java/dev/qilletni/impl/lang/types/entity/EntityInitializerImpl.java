package dev.qilletni.impl.lang.types.entity;

import dev.qilletni.api.lang.types.EntityType;
import dev.qilletni.api.lang.types.entity.EntityDefinition;
import dev.qilletni.api.lang.types.entity.EntityDefinitionManager;
import dev.qilletni.api.lang.types.entity.EntityInitializer;
import dev.qilletni.impl.lang.types.conversion.BulkTypeConversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EntityInitializerImpl implements EntityInitializer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityInitializerImpl.class);

    private final EntityDefinitionManager entityDefinitionManager;
    private final BulkTypeConversion bulkTypeConversion;

    public EntityInitializerImpl(EntityDefinitionManager entityDefinitionManager, BulkTypeConversion bulkTypeConversion) {
        this.entityDefinitionManager = entityDefinitionManager;
        this.bulkTypeConversion = bulkTypeConversion;
    }

    @Override
    public EntityType initializeEntity(String entityName, Object... args) {
        var entityDefinition = entityDefinitionManager.lookup(entityName);
        return initializeEntity(entityDefinition, args);
    }

    @Override
    public EntityType initializeEntity(String entityName, List<Object> args) {
        var entityDefinition = entityDefinitionManager.lookup(entityName);
        return initializeEntity(entityDefinition, args);
    }

    @Override
    public EntityType initializeEntity(EntityDefinition entityDefinition, Object... args) {
        return initializeEntity(entityDefinition, List.of(args));
    }

    @Override
    public EntityType initializeEntity(EntityDefinition entityDefinition, List<Object> args) {
        var adaptedArgs = bulkTypeConversion.convertToQilletniTypes(args);
        return entityDefinition.createInstance(adaptedArgs);
    }
}

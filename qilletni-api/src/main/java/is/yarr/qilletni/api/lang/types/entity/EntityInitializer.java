package is.yarr.qilletni.api.lang.types.entity;

import is.yarr.qilletni.api.lang.types.EntityType;

import java.util.List;

public interface EntityInitializer {
    
    EntityType initializeEntity(String entityName, Object... args);
    
    EntityType initializeEntity(String entityName, List<Object> args);
    
    EntityType initializeEntity(EntityDefinition entityDefinition, Object... args);
    
    EntityType initializeEntity(EntityDefinition entityDefinition, List<Object> args);
    
}

package is.yarr.qilletni.lang.types.entity;

import is.yarr.qilletni.lang.exceptions.VariableNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class EntityDefinitionManager {
    
    private final Map<String, EntityDefinition> entityDefinitionMap = new HashMap<>();
    
    private static EntityDefinitionManager musicDefinitionManager;
    
    public EntityDefinitionManager() {
        musicDefinitionManager = this;
    }
    
    public EntityDefinition lookup(String entityType) {
        if (!entityDefinitionMap.containsKey(entityType)) {
            throw new VariableNotFoundException(String.format("Entity not found with type name \"%s\"", entityType));
        }
        
        return entityDefinitionMap.get(entityType);
    }
    
    public void defineEntity(EntityDefinition entityDefinition) {
        entityDefinitionMap.put(entityDefinition.getTypeName(), entityDefinition);
    }
    
    public static EntityDefinitionManager getInstance() {
        return musicDefinitionManager;
    }
}

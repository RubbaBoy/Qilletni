package is.yarr.qilletni.lang.types.entity;

import is.yarr.qilletni.api.lang.types.entity.EntityDefinition;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.lang.exceptions.VariableNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class EntityDefinitionManagerImpl implements EntityDefinitionManager {
    
    private final Map<String, EntityDefinition> entityDefinitionMap = new HashMap<>();
    
    @Override
    public EntityDefinition lookup(String entityType) {
        if (!entityDefinitionMap.containsKey(entityType)) {
            throw new VariableNotFoundException(String.format("Entity not found with type name \"%s\"", entityType));
        }
        
        return entityDefinitionMap.get(entityType);
    }
    
    @Override
    public void defineEntity(EntityDefinition entityDefinition) {
        entityDefinitionMap.put(entityDefinition.getTypeName(), entityDefinition);
    }
}

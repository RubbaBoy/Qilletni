package is.yarr.qilletni.api.lang.types.entity;

import is.yarr.qilletni.api.lang.types.StaticEntityType;

public interface EntityDefinitionManager {

    boolean isDefined(String entityType);
    
    EntityDefinition lookup(String entityType);

    void defineEntity(EntityDefinition entityDefinition);
}

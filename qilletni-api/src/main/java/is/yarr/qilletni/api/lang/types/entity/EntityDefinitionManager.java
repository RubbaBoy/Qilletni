package is.yarr.qilletni.api.lang.types.entity;

public interface EntityDefinitionManager {

    boolean isDefined(String entityType);
    
    EntityDefinition lookup(String entityType);

    void defineEntity(EntityDefinition entityDefinition);
}

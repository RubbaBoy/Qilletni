package is.yarr.qilletni.api.lang.types.entity;

public interface EntityDefinitionManager {

    EntityDefinition lookup(String entityType);

    void defineEntity(EntityDefinition entityDefinition);
}

package dev.qilletni.api.lang.types.entity;

/**
 * A global manager for the definitions of entities, checking if they exist, and dynamically creating them.
 */
public interface EntityDefinitionManager {

    /**
     * Checks if an entity of the given name is defined.
     * 
     * @param entityType The name of the entity
     * @return If the entity is defined
     */
    boolean isDefined(String entityType);

    /**
     * Looks up the definition of a given entity name. {@link #isDefined(String)} must return <code>true</code> for
     * this to not throw an error.
     * 
     * @param entityType The name of the entity
     * @return The definition of the entity
     */
    EntityDefinition lookup(String entityType);

    /**
     * Adds an entity definition to the manager, allowing it to be created and used.
     * 
     * @param entityDefinition The definition of the entity to add
     */
    void defineEntity(EntityDefinition entityDefinition);
}

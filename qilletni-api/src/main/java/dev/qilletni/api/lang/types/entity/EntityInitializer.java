package dev.qilletni.api.lang.types.entity;

import dev.qilletni.api.lang.types.EntityType;
import dev.qilletni.api.lang.types.QilletniType;

import java.util.List;

/**
 * A global class to initialize entities, similar to {@link EntityDefinition#createInstance(QilletniType...)}, but with
 * more options such as auto conversion and lookup.
 */
public interface EntityInitializer {

    /**
     * Initializes an entity with the given name and arguments. If the constructor arguments are not of
     * {@link QilletniType}, they will be auto converted.
     * 
     * @param entityName The name of the entity to initialize
     * @param args The arguments to pass to the entity's constructor
     * @return The created {@link EntityType}
     */
    EntityType initializeEntity(String entityName, Object... args);

    /**
     * Initializes an entity with the given name and arguments. If the constructor arguments are not of
     * {@link QilletniType}, they will be auto converted.
     *
     * @param entityName The name of the entity to initialize
     * @param args The arguments to pass to the entity's constructor
     * @return The created {@link EntityType}
     */
    EntityType initializeEntity(String entityName, List<Object> args);

    /**
     * Initializes an entity with the given name and arguments. If the constructor arguments are not of
     * {@link QilletniType}, they will be auto converted.
     *
     * @param entityDefinition The definition of the entity to initialize
     * @param args The arguments to pass to the entity's constructor
     * @return The created {@link EntityType}
     */
    EntityType initializeEntity(EntityDefinition entityDefinition, Object... args);

    /**
     * Initializes an entity with the given name and arguments. If the constructor arguments are not of
     * {@link QilletniType}, they will be auto converted.
     *
     * @param entityDefinition The definition of the entity to initialize
     * @param args The arguments to pass to the entity's constructor
     * @return The created {@link EntityType}
     */
    EntityType initializeEntity(EntityDefinition entityDefinition, List<Object> args);
    
}

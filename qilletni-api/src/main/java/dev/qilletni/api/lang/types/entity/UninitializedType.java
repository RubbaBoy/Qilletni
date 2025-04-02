package dev.qilletni.api.lang.types.entity;

import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;

/**
 * Represents a field/variable in an entity that has not been initialized yet. This simply holds the type of the field,
 * and is not accessible by the user. This is special because Qilletni doesn't allow for uninitialized fields in
 * entities, so this is essentially a placeholder.
 */
public interface UninitializedType {

    /**
     * Checks if the variable is an entity (and not a native type).
     * 
     * @return If the variable is an entity
     */
    boolean isEntity();

    /**
     * Gets the {@link QilletniTypeClass} of the variable.
     * 
     * @return The type class of the variable
     */
    QilletniTypeClass<?> getNativeTypeClass();

    /**
     * If the variable is an entity, gets the definition of the entity. This will return null if {@link #isEntity()}
     * returns false.
     * 
     * @return The entity definition
     */
    EntityDefinition getEntityDefinition();

    /**
     * Gets the string name of the variable.
     * 
     * @return The type name
     */
    String getTypeName();
}

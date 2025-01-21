package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinition;

/**
 * A user-defined Qilletni type. A new instance of an {@link EntityType} is created for each instance of an entity.
 */
public non-sealed interface EntityType extends AnyType {

    /**
     * Gets the {@link EntityDefinition} that the user defined that this entity was created from. There is only one
     * {@link EntityDefinition} for each defined entity.
     * 
     * @return The {@link EntityDefinition} for the entity
     */
    EntityDefinition getEntityDefinition();

    /**
     * Gets the isolated {@link Scope} for this entity instance. All fields in the entity are present in this, along
     * with functions defined.
     * 
     * @return The {@link Scope} of the entity
     */
    Scope getEntityScope();

    /**
     * Checks if the entity's name is the given <code>typeName</code>. If not, an error is thrown.
     * 
     * @param typeName The expected name of the entity
     */
    void validateType(String typeName);
}

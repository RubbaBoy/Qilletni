package dev.qilletni.api.lang.types;

import dev.qilletni.api.lang.table.Scope;
import dev.qilletni.api.lang.types.entity.EntityDefinition;

/**
 * A Qilletni type representing the expression of an entity's name. This is used so static functions can be accessed in
 * their own scope.
 */
public non-sealed interface StaticEntityType extends QilletniType {

    /**
     * Gets the {@link EntityDefinition} that the user defined that this entity was created from. There is only one
     * {@link EntityDefinition} for each defined entity.
     *
     * @return The {@link EntityDefinition} for the entity
     */
    EntityDefinition getEntityDefinition();

    /**
     * A {@link Scope} that holds only the static functions of the entity.
     * 
     * @return The {@link Scope} containing the static functions of the entity
     */
    Scope getEntityScope();
    
}

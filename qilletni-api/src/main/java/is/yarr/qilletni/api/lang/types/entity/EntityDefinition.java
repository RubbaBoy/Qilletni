package is.yarr.qilletni.api.lang.types.entity;

import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.StaticEntityType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Defines an entity and its properties, functions, etc.
 */
public interface EntityDefinition {

    /**
     * Creates a new instance of the {@link EntityType} this definition represents, with the given constructor
     * parameters in the order defined.
     * 
     * @param constructorParams The parameters of the entity's constructor
     * @return The created {@link EntityType}
     */
    EntityType createInstance(List<QilletniType> constructorParams);

    /**
     * Creates a new instance of the {@link EntityType} this definition represents, with the given constructor
     * parameters in the order defined.
     *
     * @param constructorParams The parameters of the entity's constructor
     * @return The created {@link EntityType}
     */
    EntityType createInstance(QilletniType... constructorParams);

    /**
     * Gets a static instance of the entity, used for invoking static methods.
     * 
     * @return The created {@link StaticEntityType}
     */
    StaticEntityType createStaticInstance();

    /**
     * Gets the name of the entity.
     * 
     * @return The entity's name
     */
    String getTypeName();

    /**
     * Gets all fields of the entity that do not have a value assigned to them. These are what makes up the entity's
     * constructor, as all fields must have a value.
     * 
     * @return The name, value list of all fields without a value yet
     */
    Map<String, UninitializedType> getUninitializedParams();

    /**
     * Gets the {@link QilletniTypeClass} for the entity definition. This is specific to this definition, but still
     * holds a type of {@link EntityType}.
     * 
     * @return The type class of the entity
     */
    QilletniTypeClass<EntityType> getQilletniTypeClass();
}

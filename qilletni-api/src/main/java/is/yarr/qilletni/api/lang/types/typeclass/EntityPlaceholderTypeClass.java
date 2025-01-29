package is.yarr.qilletni.api.lang.types.typeclass;

import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinition;

/**
 * A placeholder {@link QilletniTypeClass} for entities that need a type class but are not initialized yet. This is used
 * primarily as the subtype for lists of entities if an entity has not been put in yet. 
 */
public class EntityPlaceholderTypeClass extends QilletniTypeClass<EntityType> {

    /**
     * Creates a new placeholder type class with the given entity name. This should represent an entity that already
     * exists.
     * 
     * @param typeName The name of the entity
     */
    public EntityPlaceholderTypeClass(String typeName) {
        super(null, typeName);
    }

    /**
     * Returns <code>false</code>, as this is always not a native type.
     * 
     * @return <code>false</code>
     */
    @Override
    public boolean isNativeType() {
        return false;
    }

    /**
     * Returns {@link Class<EntityType>}.
     * 
     * @return {@link Class<EntityType>}
     */
    @Override
    public Class<?> getInternalType() {
        return EntityType.class;
    }

    /**
     * Unsupported operation.
     * 
     * @return Nothing
     * @throws UnsupportedOperationException Always
     */
    @Override
    public EntityDefinition getEntityDefinition() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the given type name of this entity placeholder.
     * 
     * @return The given type name
     */
    @Override
    public String getTypeName() {
        return super.getTypeName();
    }

    /**
     * Unsupported operation.
     *
     * @return Nothing
     * @throws UnsupportedOperationException Always
     */
    @Override
    public QilletniTypeClass<?> getSubType() {
        throw new UnsupportedOperationException();
    }
}

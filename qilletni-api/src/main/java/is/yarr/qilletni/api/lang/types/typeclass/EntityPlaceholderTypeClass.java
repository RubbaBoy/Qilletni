package is.yarr.qilletni.api.lang.types.typeclass;

import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinition;

public class EntityPlaceholderTypeClass extends QilletniTypeClass<EntityType> {

    public EntityPlaceholderTypeClass(String typeName) {
        super((EntityDefinition) null, typeName);
    }

    @Override
    public boolean isNativeType() {
        return false;
    }

    @Override
    public Class<?> getInternalType() {
        return EntityType.class;
    }

    @Override
    public EntityDefinition getEntityDefinition() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTypeName() {
        return super.getTypeName();
    }

    @Override
    public QilletniTypeClass<?> getSubType() {
        throw new UnsupportedOperationException();
    }
}

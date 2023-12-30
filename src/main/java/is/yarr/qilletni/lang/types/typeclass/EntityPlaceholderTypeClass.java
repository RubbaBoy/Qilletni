package is.yarr.qilletni.lang.types.typeclass;

import is.yarr.qilletni.lang.types.EntityType;
import is.yarr.qilletni.lang.types.entity.EntityDefinition;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QilletniTypeClass<?> that = (QilletniTypeClass<?>) o;
        return Objects.equals(getTypeName(), that.getTypeName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTypeName());
    }
}

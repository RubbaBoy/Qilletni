package is.yarr.qilletni.api.lang.types.entity;

import is.yarr.qilletni.api.lang.types.entity.EntityDefinition;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

public interface UninitializedType {
    boolean isEntity();

    QilletniTypeClass<?> getNativeTypeClass();

    EntityDefinition getEntityDefinition();

    String getTypeName();
}

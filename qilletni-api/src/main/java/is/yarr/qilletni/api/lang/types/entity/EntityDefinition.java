package is.yarr.qilletni.api.lang.types.entity;

import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

import java.util.List;
import java.util.Map;

public interface EntityDefinition {
    EntityType createInstance(List<QilletniType> constructorParams);

    EntityType createInstance(QilletniType... constructorParams);

    String getTypeName();

    Map<String, UninitializedType> getUninitializedParams();

    QilletniTypeClass<EntityType> getQilletniTypeClass();
}

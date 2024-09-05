package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

/**
 * Internal types for Qilletni programs.
 */
public sealed interface QilletniType permits AnyType, ImportAliasType, StaticEntityType {
    String stringValue();
    
    default boolean qilletniEquals(QilletniType qilletniType) {
        return false;
    }

    default String typeName() {
        return getTypeClass().getTypeName();
    }
    
    QilletniTypeClass<?> getTypeClass();
}

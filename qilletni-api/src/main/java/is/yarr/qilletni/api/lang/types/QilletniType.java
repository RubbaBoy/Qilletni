package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

/**
 * Internal types for Qilletni programs.
 */
public sealed interface QilletniType permits StaticEntityType, AlbumType, BooleanType, CollectionType, DoubleType, EntityType, FunctionType, ImportAliasType, IntType, JavaType, ListType, SongType, StringType, WeightsType {
    String stringValue();
    
    default boolean qilletniEquals(QilletniType qilletniType) {
        return false;
    }

    default String typeName() {
        return getTypeClass().getTypeName();
    }
    
    QilletniTypeClass<?> getTypeClass();
}

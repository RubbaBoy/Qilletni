package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

/**
 * Internal types for Qilletni programs.
 */
public sealed interface QilletniType permits AlbumType, BooleanType, CollectionType, EntityType, FunctionType, IntType, JavaType, ListType, SongType, StringType, WeightsType {
    public abstract String stringValue();
    
    default boolean qilletniEquals(QilletniType qilletniType) {
        return false;
    }

    default String typeName() {
        return getTypeClass().getTypeName();
    }
    
    public abstract QilletniTypeClass<?> getTypeClass();
}

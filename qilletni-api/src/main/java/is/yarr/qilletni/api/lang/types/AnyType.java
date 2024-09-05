package is.yarr.qilletni.api.lang.types;

public sealed interface AnyType extends QilletniType permits AlbumType, BooleanType, CollectionType, DoubleType, EntityType, FunctionType, IntType, JavaType, ListType, SongType, StringType, WeightsType {
}

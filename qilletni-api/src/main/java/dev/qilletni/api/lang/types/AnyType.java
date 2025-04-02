package dev.qilletni.api.lang.types;

/**
 * A type under the <code>any</code> keyword.
 */
public sealed interface AnyType extends QilletniType permits AlbumType, BooleanType, CollectionType, DoubleType, EntityType, FunctionType, IntType, JavaType, ListType, SongType, StringType, WeightsType {
}

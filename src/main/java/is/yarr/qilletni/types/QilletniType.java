package is.yarr.qilletni.types;

/**
 * Internal types for Qilletni programs.
 */
public sealed interface QilletniType permits IntType, StringType, BooleanType, CollectionType, SongType, WeightsType, FunctionType {
    String stringValue();
}

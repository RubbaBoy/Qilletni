package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.exceptions.TypeMismatchException;

public class TypeUtils {

    public static <T extends QilletniType> T safelyCast(Object object, Class<T> expectedType) {
        if (!expectedType.isInstance(object)) {
            throw new TypeMismatchException("Expected type " + expectedType.getName() + " but received " + object.getClass().getName());
        }

        return expectedType.cast(object);
    }

    /**
     * Gets the {@link QilletniType} from the string type name.
     * 
     * @param stringType The type name
     * @return The found type
     */
    public static Class<? extends QilletniType> getTypeFromString(String stringType) {
        return switch (stringType) {
            case "int" -> IntType.class;
            case "boolean" -> BooleanType.class;
            case "string" -> StringType.class;
            case "collection" -> CollectionType.class;
            case "song" -> SongType.class;
            case "weights" -> WeightsType.class;
            case "function" -> throw new IllegalStateException("Cannot invoke native functions on functions");
            default -> throw new IllegalStateException("Invalid type: " + stringType);
        };
    }
}

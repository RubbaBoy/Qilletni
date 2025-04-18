package dev.qilletni.impl.lang.types;

import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.impl.lang.exceptions.TypeMismatchException;
import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Optional;

public class TypeUtils {

    public static <T extends QilletniType> T safelyCast(Object object, Class<T> expectedType) {
        if (!expectedType.isInstance(object)) {
            throw new TypeMismatchException("Expected type %s but received %s".formatted(expectedType.getSimpleName(), object == null ? "null" : object.getClass().getSimpleName()));
        }

        return expectedType.cast(object);
    }

    public static <T extends QilletniType> T safelyCast(QilletniType object, QilletniTypeClass<T> expectedType) {
        if (object == null || !object.getTypeClass().isAssignableFrom(expectedType)) {
            throw new TypeMismatchException("Expected type %s but received %s".formatted(expectedType.getTypeName(), object == null ? "null" : object.typeName()));
        }

        return (T) object;
    }

    public static <T extends QilletniType> T safelyCast(QilletniType object, QilletniTypeClass<T> expectedType, ParserRuleContext ctx) {
        if (object == null || !object.getTypeClass().isAssignableFrom(expectedType)) {
            throw new TypeMismatchException(ctx, "Expected type %s but received %s".formatted(expectedType.getTypeName(), object == null ? "null" : object.typeName()));
        }

        return (T) object;
    }

//    public static void requireType(QilletniType qilletniType, QilletniTypeClass<?> typeClass) {
//        if (!qilletniType.getTypeClass().isAssignableFrom(typeClass)) {
//            throw new TypeMismatchException("Expected type %s but got a %s".formatted(typeClass.getTypeName(), qilletniType.typeName()));
//        }
//    }

    /**
     * Gets the {@link QilletniType} from the string type name.
     * 
     * @param stringType The type name
     * @return The found type
     */
    public static QilletniTypeClass<?> getTypeFromStringOrThrow(String stringType) {
        return getTypeFromString(stringType)
                .orElseThrow(() -> new IllegalStateException("Invalid type: %s".formatted(stringType)));
    }
    
    public static Optional<QilletniTypeClass<?>> getTypeFromString(String stringType) {
        return QilletniTypeClass.types()
                .stream()
                .filter(type -> type.getTypeName().equals(stringType))
                .findFirst();
    }
    
    public static QilletniTypeClass<?> getTypeFromStringOrEntity(String stringType) {
        return getTypeFromString(stringType)
                .orElseGet(() -> QilletniTypeClass.createEntityTypePlaceholder(stringType));
    }
    
//    public static Class<? extends QilletniType> getTypeFromString(String stringType) {
//        return switch (stringType) {
//            case "int" -> IntType.class;
//            case "boolean" -> BooleanType.class;
//            case "string" -> StringType.class;
//            case "collection" -> CollectionType.class;
//            case "song" -> SongType.class;
//            case "weights" -> WeightsType.class;
//            case "function" -> throw new IllegalStateException("Cannot invoke native functions on functions");
//            default -> throw new IllegalStateException("Invalid type: " + stringType);
//        };
//    }
}

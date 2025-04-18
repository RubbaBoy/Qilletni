package dev.qilletni.api.lang.types.typeclass;

import dev.qilletni.api.lang.types.AlbumType;
import dev.qilletni.api.lang.types.AnyType;
import dev.qilletni.api.lang.types.BooleanType;
import dev.qilletni.api.lang.types.CollectionType;
import dev.qilletni.api.lang.types.DoubleType;
import dev.qilletni.api.lang.types.EntityType;
import dev.qilletni.api.lang.types.FunctionType;
import dev.qilletni.api.lang.types.ImportAliasType;
import dev.qilletni.api.lang.types.IntType;
import dev.qilletni.api.lang.types.JavaType;
import dev.qilletni.api.lang.types.ListType;
import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.lang.types.SongType;
import dev.qilletni.api.lang.types.StringType;
import dev.qilletni.api.lang.types.WeightsType;
import dev.qilletni.api.lang.types.entity.EntityDefinition;

import java.util.List;
import java.util.Objects;

/**
 * Represents the class of a QilletniType, used for type checking and conversion. This is conceptually comparable to a
 * {@link Class} in Java. An entity
 * 
 * @param <T> The {@link QilletniType} this type class represents
 */
public class QilletniTypeClass<T extends QilletniType> {
    
    public static final QilletniTypeClass<AnyType> ANY = new QilletniTypeClass<>(AnyType.class, "any");
    public static final QilletniTypeClass<IntType> INT = new QilletniTypeClass<>(IntType.class, "int");
    public static final QilletniTypeClass<DoubleType> DOUBLE = new QilletniTypeClass<>(DoubleType.class, "double");
    public static final QilletniTypeClass<BooleanType> BOOLEAN = new QilletniTypeClass<>(BooleanType.class, "boolean");
    public static final QilletniTypeClass<StringType> STRING = new QilletniTypeClass<>(StringType.class, "string");
    public static final QilletniTypeClass<CollectionType> COLLECTION = new QilletniTypeClass<>(CollectionType.class, "collection");
    public static final QilletniTypeClass<SongType> SONG = new QilletniTypeClass<>(SongType.class, "song");
    public static final QilletniTypeClass<AlbumType> ALBUM = new QilletniTypeClass<>(AlbumType.class, "album");
    public static final QilletniTypeClass<WeightsType> WEIGHTS = new QilletniTypeClass<>(WeightsType.class, "weights");
    public static final QilletniTypeClass<FunctionType> FUNCTION = new QilletniTypeClass<>(FunctionType.class, "func");
    public static final QilletniTypeClass<ListType> LIST = new QilletniTypeClass<>(ListType.class, "list");
    public static final QilletniTypeClass<JavaType> JAVA = new QilletniTypeClass<>(JavaType.class, "java");
    public static final QilletniTypeClass<ImportAliasType> IMPORT_ALIAS = new QilletniTypeClass<>(ImportAliasType.class, "");
    
    private static final List<QilletniTypeClass<?>> types = List.of(ANY, INT, DOUBLE, BOOLEAN, STRING, COLLECTION, SONG, ALBUM, WEIGHTS, FUNCTION, LIST, JAVA);

    /**
     * The internal QilletniType class of the type
     */
    private final Class<?> internalType;

    /**
     * If {@link #internalType} is {@link EntityType}, this is the entity definition of the specific type. 
     */
    private final EntityDefinition entityDefinition;
    
    private final String typeName;
    private final QilletniTypeClass<?> subType;

    private QilletniTypeClass(Class<?> internalType, String typeName) {
        this(internalType, typeName, null);
    }

    private QilletniTypeClass(Class<?> internalType, String typeName, QilletniTypeClass<?> subType) {
        this.internalType = internalType;
        this.typeName = typeName;
        this.subType = subType;
        this.entityDefinition = null;
    }

    public QilletniTypeClass(EntityDefinition entityDefinition, String typeName) {
        this(entityDefinition, typeName, null);
    }

    public QilletniTypeClass(EntityDefinition entityDefinition, String typeName, QilletniTypeClass<?> subType) {
        this.typeName = typeName;
        this.subType = subType;
        this.internalType = EntityType.class;
        this.entityDefinition = entityDefinition;
    }

    /**
     * Creates a {@link QilletniTypeClass} for a list of the given type.
     * 
     * @param subType The subtype of the list, aka what type the list contains
     * @return The created {@link QilletniTypeClass}
     */
    public static QilletniTypeClass<ListType> createListOfType(QilletniTypeClass<?> subType) {
        return new QilletniTypeClass<>(ListType.class, "list", subType);
    }

    /**
     * Used as a generic entity type for when the definition (or scope) is not yet known.
     * This should only be compared to real types and not actually used
     * 
     * @return The created {@link QilletniTypeClass}
     */
    public static QilletniTypeClass<EntityType> createEntityTypePlaceholder(String entityName) {
        return new EntityPlaceholderTypeClass(entityName);
    }

    /**
     * If this type class is native to Qilletni. This is true if this does not represent an entity.
     * 
     * @return If the type is native to Qilletni
     */
    public boolean isNativeType() {
        return entityDefinition == null;
    }

    /**
     * Gets the internal type of the type class, as {@param T}.
     * 
     * @return The class of the type
     */
    public Class<?> getInternalType() {
        return internalType;
    }

    /**
     * Checks if the internal type of this type class is assignable from the given type class. For example, if this is
     * {@link QilletniTypeClass#INT} and the given type is {@link QilletniTypeClass#ANY}, this would return <code>true</code>.
     * 
     * @param type The type to check
     * @return If this type is assignable from the given type
     */
    public boolean isAssignableFrom(QilletniTypeClass<?> type) {
        return internalType.isAssignableFrom(type.getInternalType());
    }

    /**
     * Gets the {@link EntityDefinition} if this is not a native type (via {@link #isNativeType()}).
     * 
     * @return The definition of the entity, or null if this is a native type
     */
    public EntityDefinition getEntityDefinition() {
        return entityDefinition;
    }

    /**
     * Gets the string representation of the type. If this is an entity, it will be the entity's name.
     * 
     * @return The name of the type
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * If the type contains a subtype, such as a list, this returns it.
     * 
     * @return The subtype of the type, or null if there is none
     */
    public QilletniTypeClass<?> getSubType() {
        return subType;
    }

    /**
     * Gets all {@link QilletniTypeClass}es that are native to Qilletni.
     * 
     * @return All native types
     */
    public static List<QilletniTypeClass<?>> types() {
        return types;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QilletniTypeClass<?> that)) return false;
        return Objects.equals(internalType, that.internalType) /* && Objects.equals(entityDefinition, that.entityDefinition) */ && Objects.equals(typeName, that.typeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalType, /* entityDefinition, */ typeName);
    }

    @Override
    public String toString() {
        return "QilletniTypeClass{" +
                "internalType=" + internalType +
                ", entityDefinition=" + entityDefinition +
                ", typeName='" + typeName + '\'' +
                ", subType=" + subType +
                '}';
    }
}

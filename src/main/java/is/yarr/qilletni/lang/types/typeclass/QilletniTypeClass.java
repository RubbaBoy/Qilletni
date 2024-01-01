package is.yarr.qilletni.lang.types.typeclass;

import is.yarr.qilletni.lang.types.BooleanType;
import is.yarr.qilletni.lang.types.CollectionType;
import is.yarr.qilletni.lang.types.EntityType;
import is.yarr.qilletni.lang.types.FunctionType;
import is.yarr.qilletni.lang.types.IntType;
import is.yarr.qilletni.lang.types.ListType;
import is.yarr.qilletni.lang.types.QilletniType;
import is.yarr.qilletni.lang.types.SongType;
import is.yarr.qilletni.lang.types.StringType;
import is.yarr.qilletni.lang.types.WeightsType;
import is.yarr.qilletni.lang.types.entity.EntityDefinition;

import java.util.List;
import java.util.Objects;

public class QilletniTypeClass<T extends QilletniType> {
    
    public static final QilletniTypeClass<IntType> INT = new QilletniTypeClass<>(IntType.class, "int");
    public static final QilletniTypeClass<BooleanType> BOOLEAN = new QilletniTypeClass<>(BooleanType.class, "boolean");
    public static final QilletniTypeClass<StringType> STRING = new QilletniTypeClass<>(StringType.class, "string");
    public static final QilletniTypeClass<CollectionType> COLLECTION = new QilletniTypeClass<>(CollectionType.class, "collection");
    public static final QilletniTypeClass<SongType> SONG = new QilletniTypeClass<>(SongType.class, "song");
    public static final QilletniTypeClass<WeightsType> WEIGHTS = new QilletniTypeClass<>(WeightsType.class, "weights");
    public static final QilletniTypeClass<FunctionType> FUNCTION = new QilletniTypeClass<>(FunctionType.class, "func");
    public static final QilletniTypeClass<ListType> LIST = new QilletniTypeClass<>(ListType.class, "list");
    
    private static final List<QilletniTypeClass<?>> types = List.of(INT, BOOLEAN, STRING, COLLECTION, SONG, WEIGHTS, FUNCTION, LIST);

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

    public QilletniTypeClass(Class<T> internalType, String typeName) {
        this(internalType, typeName, null);
    }

    public QilletniTypeClass(Class<T> internalType, String typeName, QilletniTypeClass<?> subType) {
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
    
    public boolean isNativeType() {
        return entityDefinition == null;
    }

    public Class<?> getInternalType() {
        return internalType;
    }

    public EntityDefinition getEntityDefinition() {
        return entityDefinition;
    }

    public String getTypeName() {
        return typeName;
    }

    public QilletniTypeClass<?> getSubType() {
        return subType;
    }
    
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

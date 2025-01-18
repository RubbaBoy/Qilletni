package is.yarr.qilletni.api.lang.types.conversion;

import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.QilletniType;

public interface TypeConverter {

    /**
     * Convert a Java object to a QilletniType object.
     * 
     * @param qilletniType The object to convert
     * @param clazz        The class of the object to convert to
     * @return The converted object
     * @param <T> The new type of the object
     */
    <T> T convertToJavaType(QilletniType qilletniType, Class<T> clazz);
    
    /**
     * Convert a QilletniType object to a Java object. If it's already a QilletniType object, it will be returned as is.
     * 
     * @param object The object to convert
     * @return The converted object
     */
    QilletniType convertToQilletniType(Object object);

    /**
     * Takes a Java record and converts it to a Qilletni entity. The entity's constructor must be in the same order as
     * the record's fields.
     * 
     * @param entityName The name of the entity to create
     * @param object The Java record
     * @return The created Qilletni entity
     */
    EntityType convertFromRecordToEntity(String entityName, Object object);
    
    /**
     * Takes a Qilletni entity and converts it to a Java record. The record's fields must be in the same order as the
     * entity's properties are defined.
     * 
     * @param entity The Qilletni entity to convert
     * @param clazz The class of the record to create
     * @return The created Java record
     * @param <T> The type of the record
     */
    <T> T convertFromEntityToRecord(EntityType entity, Class<T> clazz);

}

package is.yarr.qilletni.api.lang.types.conversion;

import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.QilletniType;

import java.util.List;

/**
 * Converts Qilletni to Java types and back.
 */
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
     * Converts a list of Java objects to a list of QilletniType objects. Useful for parameter conversions.
     * 
     * @param objects The list of objects to convert
     * @return The converted QilletniType objects
     */
    List<QilletniType> convertToQilletniTypes(List<Object> objects);

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
     * Takes a Qilletni entity and converts it to a Java record. The record's field names must be the same as the
     * entity's properties. A Java record may have a Qilletni private field mapped without the prefixing underscore,
     * however it should be noted that entities with two properties of the same name but with and without the
     * underscore (e.g. x and _x) it will not work well.
     * 
     * @param entity The Qilletni entity to convert
     * @param clazz The class of the record to create
     * @return The created Java record
     * @param <T> The type of the record
     */
    <T> T convertFromEntityToRecord(EntityType entity, Class<T> clazz);

}

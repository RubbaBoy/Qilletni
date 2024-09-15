package is.yarr.qilletni.api.lang.types.conversion;

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
    
}

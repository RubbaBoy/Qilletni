package is.yarr.qilletni.lang.types.conversion;

import is.yarr.qilletni.lang.exceptions.java.RecordConversionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecordUtility {

    private static final Map<Class<?>, RecordMetadata> recordCache = new ConcurrentHashMap<>();

    /**
     * Retrieves the record components for a given class.
     *
     * @param clazz The class to inspect.
     * @return The record components.
     */
    public static RecordComponent[] getRecordComponents(Class<?> clazz) {
        return recordCache.computeIfAbsent(clazz, RecordUtility::buildRecordMetadata).components();
    }

    /**
     * Extracts values from a record, even if fields are private.
     *
     * @param record The record instance.
     * @return An array of field values.
     */
    public static Object[] extractRecordValues(Object record) {
        if (!record.getClass().isRecord()) {
            throw new RecordConversionException("Object is not a record: %s".formatted(record.getClass().getName()));
        }

        RecordComponent[] components = getRecordComponents(record.getClass());
        return Arrays.stream(components)
                .map(component -> {
                    try {
                        Method accessor = component.getAccessor();
                        accessor.setAccessible(true);
                        return accessor.invoke(record);
                    } catch (ReflectiveOperationException e) {
                        throw new RecordConversionException("Failed to access field: %s".formatted(component.getName()));
                    }
                })
                .toArray();
    }

    /**
     * Retrieves the canonical constructor for a record class.
     *
     * @param clazz The class to inspect.
     * @return The canonical constructor for the record.
     */
    public static Constructor<?> getRecordConstructor(Class<?> clazz) {
        return recordCache.computeIfAbsent(clazz, RecordUtility::buildRecordMetadata).constructor();
    }

    /**
     * Dynamically constructs a record instance from an array of values.
     *
     * @param clazz  The record class.
     * @param values The values for the record components.
     * @param <T>    The record type.
     * @return A new instance of the record.
     */
    public static <T> T createRecord(Class<T> clazz, Object[] values) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        @SuppressWarnings("unchecked")
        T record = (T) getRecordConstructor(clazz).newInstance(values);
        return record;
    }

    /**
     * Builds the metadata for a record class.
     *
     * @param clazz The class to inspect.
     * @return The metadata for the record.
     */
    private static RecordMetadata buildRecordMetadata(Class<?> clazz) {
        if (!clazz.isRecord()) {
            throw new RecordConversionException("Class %s is not a record.".formatted(clazz.getName()));
        }

        RecordComponent[] components = clazz.getRecordComponents();
        Constructor<?> constructor;
        try {
            constructor = clazz.getDeclaredConstructor(
                    Arrays.stream(components).map(RecordComponent::getType).toArray(Class[]::new));
        } catch (NoSuchMethodException e) {
            throw new RecordConversionException("Failed to find canonical constructor for record: %s".formatted(clazz.getName()));
        }

        return new RecordMetadata(components, constructor);
    }

    /**
     * Holds metadata for a record class.
     *
     * @param components The record components.
     * @param constructor The canonical constructor.
     */
    private record RecordMetadata(RecordComponent[] components, Constructor<?> constructor) {}
}

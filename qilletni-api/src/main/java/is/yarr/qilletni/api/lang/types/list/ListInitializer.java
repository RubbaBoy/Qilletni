package is.yarr.qilletni.api.lang.types.list;

import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

import java.util.List;

/**
 * A global class to initialize lists, both from {@link QilletniType}s and native Java types.
 */
public interface ListInitializer {

    /**
     * Creates a {@link ListType} from Qilletni types.
     * 
     * @param items The items in the list, transformed via a {@link is.yarr.qilletni.api.lang.types.conversion.TypeConverter}.
     * @return The created ListType
     */
    ListType createList(List<QilletniType> items);

    /**
     * Creates a {@link ListType} from Qilletni types, with a forced list type, doing conversions as necessary.
     * 
     * @param items The items in the list
     * @param typeClass The type class to force the list to
     * @return The created ListType
     */
    ListType createList(List<QilletniType> items, QilletniTypeClass<?> typeClass);

    /**
     * Creates a {@link ListType} from a Java list.
     * 
     * @param items The items in the list, transformed via a {@link is.yarr.qilletni.api.lang.types.conversion.TypeConverter}.
     * @return The created ListType
     */
    ListType createListFromJava(List<?> items);

    /**
     * Creates a {@link ListType} from a Java list, with a forced list type, doing conversions as necessary.
     * 
     * @param items The items in the list
     * @param typeClass The type class to force the list to
     * @return The created ListType
     */
    <T extends QilletniType> ListType createListFromJava(List<?> items, QilletniTypeClass<T> typeClass);
    
}

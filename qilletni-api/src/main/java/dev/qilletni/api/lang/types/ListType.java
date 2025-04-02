package dev.qilletni.api.lang.types;

import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;

import java.util.List;

/**
 * A Qilletni type representing a List. This may contain Qilletni value.
 */
public non-sealed interface ListType extends AnyType {

    /**
     * Gets the subtype representing the list. The subtype may also be {@link AnyType}, meaning it does not need to
     * adhere to any type as a value.
     * 
     * @return The subtype of the list
     */
    QilletniTypeClass<?> getSubType();

    /**
     * Gets all the items within the list.
     * 
     * @return The contents of the list
     */
    List<QilletniType> getItems();

    /**
     * Sets the items in the list.
     * 
     * @param items The new items of the list
     */
    void setItems(List<QilletniType> items);

    /**
     * Shallow copies the list and returns a new one.
     * 
     * @return The copied list
     */
    ListType copy();
}

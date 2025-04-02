package dev.qilletni.api.lang.table;

import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;

/**
 * A defined type that can be stored in a scope. This is a {@link QilletniType} that can be looked up by name and type.
 * 
 * @param <T> The type of the symbol
 */
public interface Symbol<T extends QilletniType> {

    /**
     * The name of the symbol.
     * 
     * @return The name of the symbol
     */
    String getName();

    /**
     * gets the {@link QilletniTypeClass} of the value.
     * 
     * @return The type class of the value
     */
    QilletniTypeClass<T> getType();

    /**
     * The value of the symbol. This will never be null.
     * 
     * @return The value of the symbol
     */
    T getValue();

    /**
     * Sets the value of the symbol.
     * 
     * @param value The value of the symbol
     */
    void setValue(T value);
}

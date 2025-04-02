package dev.qilletni.api.lang.types;

/**
 * A Qilletni type representing an int value. Internally, the value is stored as a long.
 */
public non-sealed interface IntType extends AnyType {

    /**
     * Get the double long of the type.
     *
     * @return The long value
     */
    long getValue();

    /**
     * Sets the long value of the type.
     *
     * @param value The long value to set
     */
    void setValue(long value);
}

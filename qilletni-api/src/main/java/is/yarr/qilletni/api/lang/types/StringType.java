package is.yarr.qilletni.api.lang.types;

/**
 * A Qilletni type representing a String value.
 */
public non-sealed interface StringType extends AnyType {

    /**
     * Get the String value of the type.
     *
     * @return The String value
     */
    String getValue();

    /**
     * Sets the String value of the type.
     *
     * @param value The String value to set
     */
    void setValue(String value);
}

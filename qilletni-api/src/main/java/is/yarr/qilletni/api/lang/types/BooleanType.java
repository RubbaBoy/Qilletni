package is.yarr.qilletni.api.lang.types;

/**
 * A Qilletni type representing a boolean value.
 */
public non-sealed interface BooleanType extends AnyType {

    /**
     * Get the boolean value of the type.
     * 
     * @return The boolean value
     */
    boolean getValue();

    /**
     * Sets the boolean value of the type.
     *
     * @param value The boolean value to set
     */
    void setValue(boolean value);
}

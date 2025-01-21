package is.yarr.qilletni.api.lang.types;

/**
 * A Qilletni type representing a double value.
 */
public non-sealed interface DoubleType extends AnyType {

    /**
     * Get the double value of the type.
     * 
     * @return The double value
     */
    double getValue();

    /**
     * Sets the double value of the type.
     * 
     * @param value The double value to set
     */
    void setValue(double value);
}

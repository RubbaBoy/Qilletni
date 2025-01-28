/**
 * An entity that may either hold a value or be empty. The value being held may be anything.
 */
entity Optional {

    /**
     * The value of the optional.
     */
    any _value
    
    /**
      * If the optional has a value.
      *
      * @type boolean
      */
    boolean _hasValue
    
    /**
     * Creates a new optional with the given value and if it has a value.
     * It is strongly recommended to just use `fromValue()` or `fromEmpty()`
     */
    Optional(_value, _hasValue)
    
    /**
     * Creates an optional from a value.
     *
     * @param value The value to create the optional from. This may be any type
     * @returns[@type std.Optional] The optional created from the value
     */
    static fun fromValue(value) {
        return new Optional(value, true)
    }
    
    /**
     * Creates an empty optional.
     *
     * @returns[@type std.Optional] An empty optional
     */
    static fun fromEmpty() {
        // Using empty is a little abusive, however an empty java pointer is acceptable for this
        return new Optional(empty, false)
    }
    
    /**
     * Checks if the optional has a value.
     *
     * @returns[@type boolean] If the optional has a value
     */
    fun hasValue() {
        return _hasValue
    }
    
    /**
     * Gets the value of the optional. This will stop the program if the optional is empty, so check first via
     * `hasValue()`.
     *
     * @returns The value of the optional, any type
     */
    fun getValue() {
        if (_hasValue) {
            return _value
        }
    }
    
    /**
     * Clears the value of the optional.
     */
    fun clearValue() {
        _hasValue = false
        _value = empty
    }
    
    /**
     * Sets the value of the optional.
     *
     * @param value The value to set
     */
    fun setValue(value) {
        _hasValue = true
        _value = value
    }
    
    fun toString() {
        if (_hasValue) {
            return "Optional(value = %s)".format([_value])
        } else {
            return "Optional(empty)"
        }
    }
}

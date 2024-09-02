
/**
 * Creates an empty [@type @java java.util.Optional].
 *
 * @returns[@type @java java.util.Optional] An empty Optional
 */
native fun _emptyOptional()

/**
 * Creates an empty [@type @java java.util.Optional] holding a Qilletni type as its value.
 *
 * @param value The value to hold, of any type
 * @returns[@type @java java.util.Optional] An empty Optional
 */
native fun _optionalFrom(value)

/**
 * An entity that may either hold a value or be empty. The value being held may be anything.
 */
entity Optional {

    /**
     * The value of the optional.
     *
     * @type @java java.util.Optional
     */
    java _value
    
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
        return new Optional(_optionalFrom(value), true)
    }
    
    /**
     * Creates an empty optional.
     *
     * @returns[@type std.Optional] An empty optional
     */
    static fun fromEmpty() {
        return new Optional(_emptyOptional(), false)
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
     * Gets the value of the optional.
     *
     * @returns The value of the optional, any type
     */
    native fun getValue()
    
    /**
     * Clears the value of the optional.
     */
    fun clearValue() {
        _hasValue = false
        _value = _emptyOptional()
    }
    
    /**
     * Sets the value of the optional.
     *
     * @param value The value to set
     */
    fun setValue(value) {
        _hasValue = true
        _value = _optionalFrom(value)
    }
}

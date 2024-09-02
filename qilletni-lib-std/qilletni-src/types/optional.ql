
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
     * @type @java java.util.Optional
     */
    java _value
    boolean _hasValue
    
    Optional(_value, _hasValue)
    
    static fun fromValue(value) {
        return new Optional(_optionalFrom(value), true)
    }
    
    static fun fromEmpty() {
        print("fromEmpty() invoked!")
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

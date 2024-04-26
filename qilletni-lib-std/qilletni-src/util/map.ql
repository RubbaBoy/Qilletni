// Creates an empty java HashMap instance
native fun _emptyJavaMap()

// A HashMap. Keys and values may be of any type
entity Map {
    java _map = _emptyJavaMap()
    
    // Puts a ky and a value into the map
    native fun put(key, value)
    
    // Gets a value from the map by its key
    // This will throw an error if the key is not found
    native fun get(key)
    
    // Checks if the key is in the map
    native fun containsKey(key)
    
    // Checks if the value is anywhere in the map
    native fun containsValue(key)
    
    // Gets a list of all keys in the map
    native fun keys()
    
    // Gets a list of all values in the map
    native fun values()
}

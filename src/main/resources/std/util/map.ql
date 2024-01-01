native fun emptyJavaMap()

entity Map {
    java map = emptyJavaMap()

    Map()
    
    native fun put(key, value)
    
    native fun get(key)
    
    native fun containsKey(key)
    
    native fun containsValue(key)
}

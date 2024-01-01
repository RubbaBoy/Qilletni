entity Map {
    Map()
    
    native fun put(key, value)
    
    fun putt(key, value) {
        print("key = " + key)
        print("value = " + value)
    }
}

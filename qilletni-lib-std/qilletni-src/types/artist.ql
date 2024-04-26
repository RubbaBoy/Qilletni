entity Artist {
    string _id
    string _name
    
    Artist(_id, _name)
    
    fun getId() {
        return _id    
    }
    
    fun getName() {
        return _name
    }
}

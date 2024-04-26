entity User {
    string _id
    string _name
    
    User(_id, _name)
    
    fun getId() {
        return _id    
    }
    
    fun getName() {
        return _name
    }
}

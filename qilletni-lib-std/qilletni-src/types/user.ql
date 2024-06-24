/**
 * An end user/listener of a song provider that may own playlists.
 */
entity User {

    /**
     * The ID of the user.
     */
    string _id
    
    /**
     * The name of the user.
     */
    string _name
    
    /**
     * Creates a new user with the given ID and name.
     * The [@param id] is a string that is identifiable by the provider which owns this user. An example of this is
     * a standard Spotify ID.
     *
     * @param[@type string] id The ID of the user
     * @param[@type string] name The name of the user
     */
    User(_id, _name)
    
    /**
     * Gets the ID of the user.
     *
     * @returns[@type string] The ID of the user
     */
    fun getId() {
        return _id    
    }
    
    /**
     * Gets the name of the user.
     *
     * @returns[@type string] The name of the user
     */
    fun getName() {
        return _name
    }
}

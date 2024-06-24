/**
 * An artist who creates songs.
 */
entity Artist {

    /**
     * The ID of the artist.
     */
    string _id
    
    /**
     * The name of the artist.
     */
    string _name
    
    /**
     * Creates a new artist with the given ID and name.
     * The [@param id] is a string that is identifiable by the provider which owns this artist. An example of this is
     * a standard Spotify ID.
     *
     * @param[@type string] id The ID of the artist
     * @param[@type string] name The name of the artist
     */
    Artist(_id, _name)
    
    /**
     * Gets the ID of the artist.
     *
     * @returns[@type string] The ID of the artist
     */
    fun getId() {
        return _id    
    }
    
    /**
     * Gets the name of the artist.
     *
     * @returns[@type string] The name of the artist
     */
    fun getName() {
        return _name
    }
}

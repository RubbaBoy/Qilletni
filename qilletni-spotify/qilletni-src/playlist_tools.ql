/**
 * Creates a new playlist with the given name.
 * 
 * @param[@type string] name The name of the playlist
 * @returns[@type collection] The created playlist
 */
native fun createPlaylist(name)

/**
 * Creates a new playlist with the given name and description.
 * 
 * @param[@type string] name The name of the playlist
 * @param[@type string] desc The description of the playlist
 * @returns[@type collection] The created playlist
 */
native fun createPlaylist(name, desc)

/**
 * Adds the given song list to the given playlist.
 * 
 * @param[@type collection] playlist The playlist to add the songs to
 * @param[@type list] songList The list of songs to add to the playlist
 */
native fun addToPlaylist(playlist, songList)

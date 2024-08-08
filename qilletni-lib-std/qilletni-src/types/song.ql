/**
 * Gets the URL of the song. If it was not defined with a URL, an empty string is returned.
 *
 * @returns[@type string] The URL of the song
 */
native fun getUrl() on song

/**
 * Gets the ID of the song. This is an ID identifiable by the provider which owns this song.
 *
 * @returns[@type string] The ID of the song
 */
native fun getId() on song

/**
 * Gets the [@type std.Artist] entity of the primary artist on the song
 *
 * @returns[@type std.Artist] The first artist of a song
 */
native fun getArtist() on song

/**
 * Gets all [@type std.Artist] entities on the song.
 *
 * @returns[@type list] A list of artists on the song
 */
native fun getAllArtists() on song

/**
 * Gets the [@type std.Album] the song is on.
 *
 * @returns[@type std.Album] The album of the song
 */
native fun getAlbum() on song

/**
 * Gets the name of the song.
 *
 * @returns[@type string] The name of the song
 */
native fun getTitle() on song

/**
 * Gets the duration of the song in milliseconds.
 *
 * @returns[@type int] The duration of the song
 */
native fun getDuration() on song

// Gets the URL of the song. If it was not defined with a URL, an empty string is returned.
native fun getUrl() on song

// Gets the ID of the song
native fun getId() on song

// Gets the Artist entity of the primary artist on the song
native fun getArtist() on song

// Gets a list of Artist entities on the song
native fun getAllArtists() on song

// Gets the Album the song is on
native fun getAlbum() on song

// Gets the title of the song
native fun getTitle() on song

// Gets the duration of the song in milliseconds
native fun getDuration() on song

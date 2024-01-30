// The default "play" keyword but as a function, which will call the default "play" action as if no redirection
// happened.
native fun defaultPlay(songToPlay)

// Starts making all 'play's add to a song list
native fun redirectPlayToList(songList)

// Starts making all 'play's invoke the given function with 1 parameter of the song
native fun redirectPlayToFunction(fn)

// Stops all 'play's from any set action and returns it back to its default action 
native fun redirectReset()

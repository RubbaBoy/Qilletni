/**
 * Play a song via the default "play" keyword invoked as a function, which will call the default "play" action as if no
 * redirection happened.
 *
 * @param[@type song] songToPlay The song 
 */
native fun defaultPlay(songToPlay)

/**
 * Starts making all `play`s add to a song list instead of the previous `play` action.
 *
 * @param[@type list] songList The list to add songs to 
 */
native fun redirectPlayToList(songList)

/**
 * Starts making all `play`s invoke the given function with 1 parameter of the song.
 *
 * @param fn The function to call with the song as an argument
 */
native fun redirectPlayToFunction(fn)

/**
 * Stops all 'play's from any set action and returns it back to its default action.
 */ 
native fun redirectReset()

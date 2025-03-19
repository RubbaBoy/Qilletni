/**
 * Calls a callback function when the currently playing song is changed on a Spotify account, with a polling time of
 * 5000ms.
 *
 * @param[@type function] fn The function to call with the song as an argument
 */
fun onSongPlay(fn) {
    onSongPlay(fn, 5000)
}

/**
 * Calls a callback function when the currently playing song is changed on a Spotify account.
 *
 * @param[@type function] fn The function to call with the song as an argument
 * @param[@type int] pollTime The milliseconds to wait between polling
 */
native fun onSongPlay(fn, pollTime)

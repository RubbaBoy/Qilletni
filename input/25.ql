import "!spotify:play_redirect.ql"
import "!spotify:playlist_tools.ql"

// Route all played songs to this list
song[] songList = []
redirectPlayToList(songList)

// Play songs
play "Chill Bruh Moment" collection by "rubbaboy" order[shuffle] limit[25]

// Add played songs to a playlist
collection myPlaylist = createPlaylist("Qilletni Playlist 1")
addToPlaylist(myPlaylist, songList)

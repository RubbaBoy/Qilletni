import "!spotify:play_redirect.ql"
import "!spotify:playlist_tools.ql"

// Route all played songs to this list
song[] songList = []
redirectPlayToList(songList)

weights powerRotation =
    | 20% "Empty Mirror" by "Colony Collapse"
    | 10% "Terror" by "Dread Engine"
    | 10% "Ghost In The Graveyard" by "Divine Fallacy"
    | 10% "Dead Dreams" by "Glasswaves"
    | 10% "Impulse" by "Harroway"
    | 10% "Hell (I let the Devil In)" by "Breakwaters"
    | 10% "Anxiety" by "Breakwaters"
    | 5% "Heavy Rain" by "Konami Kode"
    | 5% "Puke" by "No Life"
    | 5% "Blood Petals (feat. Julian Latouche)" by "Revoid"
    | 5% "All or Nothing" by "Foundations"

// Play songs
play "My Playlist #59" collection by "rubbaboy" order[shuffle] weights[powerRotation] limit[6h]

// Add played songs to a playlist
collection myPlaylist = createPlaylist("Metal Day Queue 2")
addToPlaylist(myPlaylist, songList)

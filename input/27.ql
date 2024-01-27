import "!spotify:play_redirect.ql"
import "!spotify:playlist_tools.ql"

song[] songList = []
redirectPlayToList(songList)

// |   - Disallows song repeats, allows weight repeats
// |!  - Allows for song repeats
// |~  - Disallows weight repeats (and track)

weights subRotation =
    | 25% "Anxiety" by "Breakwaters"
    | 75% "Hell (I Let The Devil In)" by "Breakwaters"
    
weights powerRotation =
//    | 50% "3MINQxDHqX6fvjARVvLVIM" 
//    | 20% "Anxiety" by "Breakwaters"
//    | 40% ["Anxiety" by "Breakwaters", "Hell (I Let The Devil In)" by "Breakwaters"]
    | 50% song["3MINQxDHqX6fvjARVvLVIM", "2kzPGfQ6D7zcA0i5bAvixn"]
//    | 50% subRotation
//    | 20% "weights" collection by "rubbaboy"

// Play songs
play "qtest" collection by "rubbaboy" weights[powerRotation] limit[200]

for (track : songList) {
    print(track)
}

import "!spotify:play_redirect.ql"
import "!spotify:playlist_tools.ql"

// Route all played songs to this list
song[] songList = []
redirectPlayToList(songList)

// |   - Disallows song repeats, allows weight repeats
// |!  - Allows for song repeats
// |~  - Disallows weight repeats (and track)

weights powerRotation =
    |! 10% "Empty Mirror" by "Colony Collapse"
    | 85% ["Wildfire" by "Icreatedamonster",
            "Burn Victim" by "Methwitch",
            "Man Made Disaster" by "Divine Fallacy",
            "Firestarter" by "SUMR",
            "Dead Dreams" by "Glasswaves",
            "Hell (I let the Devil In)" by "Breakwaters",
            "Anxiety" by "Then It Ends",
            "Distance" by "Sleep Waker",
            "Claustrophobic" by "Before I Turn",
            "White Lady" by "Before I Turn",
            "Some Things to Chase" by "Lights & Apparitions"]
    | 5% ["Heavy Rain" by "Konami Kode",
            "Puke" by "No Life",
            "Decay" by "Elwood Stray",
            "Blood Petals (feat. Julian Latouche)" by "Revoid",
            "All or Nothing" by "Foundations",
            "Peacekeeper" by "Confessions of a Traitor",
            "Melatonin" by "Sleep Waker"]

weights metalWeights =
//    |! 100% "Distance" by "Sleep Waker"
   |~ 50% powerRotation

// Play songs
play "My Playlist #59" collection by "rubbaboy" weights[metalWeights] limit[8h]

// Add played songs to a playlist

Date today = newDateNow()
collection myPlaylist = createPlaylist("Metal Day Queue " + today.getMonth() + "/" + today.getDay())
addToPlaylist(myPlaylist, songList)

print(songList)

// Analyze songs played after adding to playlist

entity ArtistStats {
    Artist artist
    int songs = 1
    
    ArtistStats(artist)
}

Map allArtistsPlayed = new Map()

for (songPlayed : songList) {
    Artist artist = songPlayed.getArtist()
    print(artist)
    
    if (allArtistsPlayed.containsKey(artist)) {
        ArtistStats stats = allArtistsPlayed.get(artist)
        print("For " + stats + " adding 1!")
        stats.songs++
        print("Now is: " + stats.songs)
    } else {
        ArtistStats stats = new ArtistStats(artist)
        allArtistsPlayed.put(artist, stats)
    }
}

print("Analysis of artists in the created playlist:")
for (artist : allArtistsPlayed.keys()) {
    ArtistStats stats = allArtistsPlayed.get(artist)
    print("\t" + artist.getName() + " \t- " + stats.songs)
}

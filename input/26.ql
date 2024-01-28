import "!spotify:play_redirect.ql"
import "!spotify:playlist_tools.ql"

// Route all played songs to this list
song[] songList = []
redirectPlayToList(songList)

weights powerRotation =
    | 20% "Empty Mirror" by "Colony Collapse"
    | 50% ["Terror" by "Dread Engine",
            "Ghost In The Graveyard" by "Divine Fallacy",
            "Man Made Disaster" by "Divine Fallacy",
            "Dead Dreams" by "Glasswaves",
            "Impulse" by "Harroway",
            "Hell (I let the Devil In)" by "Breakwaters",
            "Anxiety" by "Breakwaters",
            "Distance" by "Sleepwalker"]
    | 30% ["Heavy Rain" by "Konami Kode",
            "Puke" by "No Life",
            "Blood Petals (feat. Julian Latouche)" by "Revoid",
            "All or Nothing" by "Foundations"]

weights metalWeights =
//    |! 100% "Distance" by "Sleep Waker"
   |~ 50% powerRotation

// Play songs
play "My Playlist #59" collection by "rubbaboy" weights[metalWeights] limit[200]

// Add played songs to a playlist
//collection myPlaylist = createPlaylist("Metal Day Queue (With |~)")
//addToPlaylist(myPlaylist, songList)

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

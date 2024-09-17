import "spotify:recommendations.ql"
import "spotify:play_redirect.ql"
import "spotify:playlist_tools.ql"
import "std:types/collections/stack.ql"

weights powerRotation =
    | 85% ["Monarch" by "Glasswaves",
            "Wants I Need" by "156/Silence",
            "Anti-Saviour" by "Voluntary Victim",
            "Millstone" by "ROSARY",
            "Hell (I let the Devil In)" by "Breakwaters",
            "Distance" by "Sleep Waker",
            "Claustrophobic" by "Before I Turn"]
    | 15% ["Heavy Rain" by "Konami Kode",
            "Decay" by "Elwood Stray",
            "All or Nothing" by "Foundations"]

weights metalWeights =
   |~ 30% powerRotation

collection metalSongs = "My Playlist #59" collection by "rubbaboy" weights[metalWeights]

song[] songList = []
redirectPlayToList(songList)

// All recommended songs
Stack recommendations = new Stack()

fun generateUniqueRecommendations() {
    Recommender recommender = new Recommender()
            ..seedTracks = ["Monarch" by "Glasswaves",
                            "Wants I Need" by "156/Silence",
                            "Anti-Saviour" by "Voluntary Victim",
                            "Spiral" by "Feyn Entity",
                            "Hell (I let the Devil In)" by "Breakwaters"]
            ..targetEnergy = 1.0
            ..targetPopularity = 10

    song[] recs = recommender.recommend(100)

    for (rec : recs) {
        if (!metalSongs.containsArtist(rec.getArtist())) {
            recommendations.push(rec)
        }
    }
    
    print("Generated " + recommendations.size() + " unique recommendations")
}

generateUniqueRecommendations()

// Play 2 shuffled songs from metal playlist, then 3 recommendations
for (recommendations.size() >= 3) {
    play metalSongs limit[2]

    for (i..3) {
        play recommendations.pop()
    }
}

// Add all played songs to a new playlist
Date date = Date.now()
collection myPlaylist = createPlaylist("Playlist w/ recs %d/%d".format([date.getMonth(), date.getDay()]))

addToPlaylist(myPlaylist, songList)
print("Created a playlist with %s songs".format([songList.size()]))

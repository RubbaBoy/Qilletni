import "spotify:play_redirect.ql"
import "spotify:playlist_tools.ql"

redirectPlayToFunction(print)

fun getSomeSong() {
    print("Getting a song!")
    
    if (random(1, 10) > 5) {
        return "Hell (I let the Devil In)" by "Breakwaters"    
    }
    
    return "God Knows" by "Knocked Loose"
}

// Normally, if a track has a % weight, it removes the track from the collection and THEN performs % calculations.
// With a function as a %, no tracks are removed and the % calculation still happens
weights demo =
            |! 75% getSomeSong()

collection runtimeCollection = collection(["Kiss Me Better" by "Julie Bergan",
                                            "Strawberry Dream" by "Dagny",
                                            "Anxiety" by "Then It Ends",
                                            "Distance" by "Sleep Waker",
                                            "Claustrophobic" by "Before I Turn"]) weights[demo]

play runtimeCollection limit[30]

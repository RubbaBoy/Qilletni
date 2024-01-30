import "!spotify:play_redirect.ql"
import "!spotify:playlist_tools.ql"

fun playCallback(songPlayed) {
    print("Playing song: " + songPlayed.getTitle())
    
    defaultPlay(songPlayed)
}

// Route all played songs to this function
redirectPlayToFunction(playCallback)

play "Impulse" by "Harroway"
play "Hell (I let the Devil In)" by "Breakwaters"
play "Anxiety" by "Breakwaters"
play "Distance" by "Sleep Waker"

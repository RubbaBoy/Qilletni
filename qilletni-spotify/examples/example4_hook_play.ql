import "spotify:hooks.ql"

provider "spotify"

fun songPlayCallback(sng) {
    printf("Playing:\t%s - %s", [sng.getTitle(), sng.getArtist().getName()])
}

onSongPlay(songPlayCallback)

// Halt program forever, running background tasks
processBackground()

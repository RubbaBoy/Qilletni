import "!std/core.ql"

fun printList(a) {
    print("Song names/artists:")
    for (mySong : songs) {
        print(mySong.getTitle() + " - " + mySong.getArtist().getName())
    }
}

song[] songs = song["God Knows" by "Knocked Loose", "01UFDRR8Mv3tYSqauSdyTl", "Compass" by "Counterparts"]

printList(song["God Knows" by "Knocked Loose", "01UFDRR8Mv3tYSqauSdyTl", "Compass" by "Counterparts"])

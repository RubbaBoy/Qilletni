import "spotify:blocking_queue.ql"

provider "spotify"

enableBlockingQueue()

play "Top Song Playlist" collection by "rubbaboy" limit[20]

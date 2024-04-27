import "spotify:play_redirect.ql" as spotify
import "spotify:playlist_tools.ql" as spotify

song[] songList = []
redirectPlayToFunction(print)

weights demo =
            | "Anxiety" by "Then It Ends"

collection runtimeCollection = collection(["Hell (I let the Devil In)" by "Breakwaters",
                                            "Anxiety" by "Then It Ends",
                                            "Distance" by "Sleep Waker",
                                            "Claustrophobic" by "Before I Turn"])

play runtimeCollection limit[30]

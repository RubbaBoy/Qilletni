import "std/core.ql"

weights metal_weights =
    | 3x "God Knows" by "Knocked Loose"
    | 2x "https://open.spotify.com/track/3M1RZOhzt4lG3vpSYwffhe?si=aa4cff0ba07a4a37"
    | 10% "US" by "Apex Alpha"

play collection "My Playlist #59" created by "rubbaboy" order[shuffle] weights[metal_weights]

collection metal_playlist = "My Playlist #59" created by "rubbaboy" order[sequential]
play collection metal_playlist limit[100]

print(metal_playlist)

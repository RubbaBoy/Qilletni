import balls.ql;

play "Empty Mirror" by "Colony Collapse"

for (i..19) {
    if (i % 2 == 0) {
        play nu_disco limit[3]
    } else {
        play polish_rave limit[3]
    }
}


weights metal_weights =
    | 3x "God Knows" by "Knocked Loose"
    | 2x "https://open.spotify.com/track/3M1RZOhzt4lG3vpSYwffhe?si=aa4cff0ba07a4a37"
    | 10% "US" by "Apex Alpha"
    ;
    
fun hi(a, b, c) {
    // hi
    int i = 0;
    string s = "Hello World!";
    play "https://aa"
    play "Empty Mirror" by "Colony Collapse"
    
    play collection "https://open.spotify.com/playlist/6CQ65y99IfXGV62AnQykDG?si=5d4b35f0f1904a91" order[shuffle] weights[metal_weights]
    play collection "My Playlist #59" by "rubbaboy" weights[metal_weights] limit[10]
}

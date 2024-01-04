entity Album {
    string id
    string name
    Artist artist
    Artist[] artists
    
    Album(id, name, artist, artists)
}

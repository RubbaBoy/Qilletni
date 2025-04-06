import "lastfm:lastfm.ql"

provider "lastfm"

print("\tProvider is Last.FM")

Page page = new Page()
                ..page = 1
                ..count = 1

song topSong = getTopTracks("RubbaBoy", "7day", page).data[0]

print(topSong)

provider "spotify" {
    print("\tProvider is Spotify")
    
    print(topSong.getId())
}

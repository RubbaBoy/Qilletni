import "!std/core.ql"

fun bar(str) on string {
    print("my string: " + str)
}

//song foo = "My Playlist #59" collection by "rubbaboy"
song foo = "https://open.spotify.com/track/0mFaWgcWph8oldTKMnWeQW?si=a231642cadc74ca0"

album myAlb = foo.getAlbum()

"hello".bar()

print("Album data:")
print("\t id = " + myAlb.getId())
print("\t name = " + myAlb.getName())
print("\t artist = " + myAlb.getArtist())
print("\t all artists = " + myAlb.getAllArtists())

print("\nSong data:")
print("\t id = " + foo.getId())
print("\t name = " + foo.getTitle())
print("\t artist = " + foo.getArtist())
print("\t all artists = " + foo.getAllArtists())
print("\t duration = " + foo.getDuration())

print("\nAll artists:")
for (artist : foo.getAllArtists()) {
    print("\t " + artist.getName())
}

import "!std/core.ql"

//string foo = "foo"

fun bar(str) on string {
    print("my string: " + str)
}

//song foo = "My Playlist #59" collection by "rubbaboy"
song foo = "God Knows" by "Knocked Loose"

//album a =  "A Tear In The Fabric of Life" album by "Knocked Loose"

print(foo)
print(foo.getAlbum())

import "!std/core.ql"

//string foo = "foo"

fun bar(str) on string {
    print("my string: " + str)
}

song foo = "My Playlist #59" collection by "rubbaboy"
song foo = "Counting Worms" by "Knocked Loose"

album a =  "A Tear In The Fabric of Life" album by "Knocked Loose"


string[] str = ["one", "two", "three", "four", "balls"]

for (i : str) {
    if (i == "two") {
        print("Got two!")
    }
    
    print(i.toUpper())
}

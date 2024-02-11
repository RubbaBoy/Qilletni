// Switches the provider until it is changed again
provider "spotify"
play "Counting Worms" by "Knocked Loose"
play "Anxiety" by "Breakwaters"


// Switches provider only in the block
provider "spotify" {
    play "Counting Worms" by "Knocked Loose"
    play "Anxiety" by "Breakwaters"
}


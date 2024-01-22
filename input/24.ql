import "!std:core.ql"

play "Terror" by "Dread Engine"
play "Anxiety" by "Breakwaters"
play "God Knows" by "Knocked Loose"

weights qtestWeights =
    | 5% "lmho" by "flor"

print("Playing weighted qtest:")

play "Chill Bruh Moment" collection by "rubbaboy" order[shuffle] weights[qtestWeights] limit[100]

//print("Playing 3 from qtest:")
//
//play "qtest" collection by "rubbaboy" order[shuffle] limit[3]

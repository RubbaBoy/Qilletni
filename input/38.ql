entity Coord {
    int x
    int y
    
    Coord(x, y)
}

Coord c1 = new Coord(1, 2)
Coord c2 = new Coord(1, 2)

print("c1 = %s".format([c1]))
print("c2 = %s".format([c2]))

import "!std/core.ql"

Map map = new Map()
map.putt(1, 2)
map.put(1, 2)


fun balls(map, a) on Map {
    print("balls! " + a)
    print(map)
}

fun foo() {}


map.balls("woahh")
int d = 123

print(map)

print(random(1, 1000))
//print("a = " + a)


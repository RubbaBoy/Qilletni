import "!std/core.ql"

Map map = new Map()
map.putt(1, 2)
map.put(1, 2)
int a = 123

fun balls(map, a) on Map {
    print("balls! " + a)
//    print(mapp)
}

fun foo() {}

map.balls("woahh")

print(random(1, 1000))
print("a = " + a)

string abb = foo()

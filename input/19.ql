import "!std/core.ql"

Map map = new Map()
map.put(1, 200)

fun foo(map) on Map {
    print("balls! " + map)
}

print(map)
print(map.get(1))

print(map.containsKey("hi"))
map.put("hi", 100)

print(map.containsKey("hi"))
print(map.get("hi"))

map.foo()

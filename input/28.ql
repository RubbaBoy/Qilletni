entity Foo {
    int bar
    
    Foo(bar)
}

Foo foo = new Foo(420)

Map map = new Map()
map.put(foo, 12345)

print(map)

print(map.containsKey(foo))

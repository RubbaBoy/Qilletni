int foo = 0

foo++
foo += 10

print(foo) // 11

foo -= 2

print(foo) // 9

print(foo += 2) // 9
print(foo) // 11

print("With entity now!")

entity Bar {
    int i
    
    Bar(i)
}

Bar bar = new Bar(0)

print(bar.i)

bar.i += 10

print(bar.i)

bar.i++

print(bar.i)

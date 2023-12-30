import "std/core.ql"

entity Pair {
    int x
    int y

    Pair(x, y)

    fun sum() {
        return x + y
    }

    fun product() {
        return x * y
    }
}

entity Foo {
    Foo()
}

int i = 0
int j = 1

i = j

Pair pair = new Pair(2, 10)

Foo foo = new Foo()

print("Constructed pair: " + pair)
print("foo = " + foo)

foo = pair // fails

print("foo = " + foo)

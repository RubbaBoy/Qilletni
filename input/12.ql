import "std/core.ql"

entity Node {
    string foo
    string bar = "barrrr"
    int baz

    // params without a value MUST be in constructor
    Node(foo, baz)

    fun dump() {
        print("foo = " + foo)
        print("bar = " + bar)
        print("baz = " + baz)
    }
}

Node node = new Node("foo", 10)

print("my node = " + node)
node.dump()

node.foo = "balls"

print("node.foo = " + node.foo)

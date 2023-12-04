import "std/core.ql"

native fun length(str) on string

fun sayHi(str) on string {
    print("Hello string, " + str)
}

string foo = "Adam"

print(foo)
print(foo.length())
foo.sayHi()

import "std/core.ql"

fun foo(a) {
    int b = a * 10
    print(a + " * 10 is: " + b)
    bar(b)
    print("b again is " + b)
}

fun bar(c) {
    string b = "Hey "
    print(b + c)
}

// 0 1 2 3 4
for (i..5) {
    foo(i)
}

print("Even numbers from 0-99:")

for (i..100) {
    if (i % 2 == 0) {
        print(i)
    }
}

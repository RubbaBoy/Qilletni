import "!std/core.ql"

string foo = "Hello World!"

print(foo)
print(foo.length())
print(foo.contains("World"))
print(foo.contains("Bar"))
print(foo.substring(2))
print(foo.substring(2, 5))
print(foo.toUpper())
print(foo.toLower())

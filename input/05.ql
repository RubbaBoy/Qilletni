import "std/core.ql"

string a = "Hello "
string b = "World!"

int rand = random(0, 100)
string c = ("Random int: " + rand)

print(c)

print("rand + 10: " + (rand + 10))

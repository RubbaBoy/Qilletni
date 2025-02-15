//
// test_ops.ql
//
// Demonstrates arithmetic precedence, increments, range loops,
// relational operators, assignment operators, logical NOT, and dot-access.
//
// Each print statement shows "(should be ... )" for quick verification.
//

// 1) Basic arithmetic and assignment
int x = 2
print("Initial x = " + x + " (should be 2)")  // 2

x++
print("After x++, x = " + x + " (should be 3)")  // 3

x += 5
print("After x += 5, x = " + x + " (should be 8)")  // 8

x--
print("After x--, x = " + x + " (should be 7)")  // 7

x -= 2
print("After x -= 2, x = " + x + " (should be 5)")  // 5

// Multiplication vs. addition precedence
int y = 3 + 4 * 5
print("3 + 4 * 5 => " + y + " (should be 23)")

// Floor division (/~) vs. floating division (/)
int fd = 7 /~ 2
print("7 /~ 2 => " + fd + " (should be 3)")

double div = 7 / 2
print("7 / 2 => " + div + " (should be 3.5)")

int modVal = 14 % 3
print("14 % 3 => " + modVal + " (should be 2)")

// 2) The range operator in a 'for' loop
print("Range loop from 0..3 => (should print i = 0, 1, 2, 3)")
for (i..3) {
    print("  i = " + i)
}

// 3) Logical NOT
boolean flag = true
print("flag = " + flag + " (should be true)")

flag = !flag
print("NOT flag => " + flag + " (should be false)")

// 4) Relational operators
print("5 > 3 => " + (5 > 3) + " (should be true)")
print("5 >= 3 => " + (5 >= 3) + " (should be true)")
print("5 < 10 => " + (5 < 10) + " (should be true)")
print("5 <= 2 => " + (5 <= 2) + " (should be false)")
print("5 == 5 => " + (5 == 5) + " (should be true)")
print("5 != 2 => " + (5 != 2) + " (should be true)")

// 5) Entity with dot access and postfix increments
entity Person {
    string name
    int age

    // Constructor
    Person(name, age)

    // Simple instance function
    fun greet() {
        print("Hi, my name is " + name + ", and I'm " + age + " years old.")
    }
}

Person p = new Person("Alice", 25)
p.age++
printf("After p.age++, p.age = %d (should be 26)", [p.age])

p.age += 5
p.greet()  // "Hi, my name is Alice, and I'm 31 years old."

int a = 0
int b = 0

a = b = 5

printf("a = %d (should be 5)", [a])
printf("b = %d (should be 5)", [b])

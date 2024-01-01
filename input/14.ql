import "!std/core.ql"

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

Pair pair = new Pair(2, 10)

print("Constructed pair: " + pair)

int sum = pair.sum()
print("sum = " + sum)

print("product = " + pair.product())

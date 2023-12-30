import "std/core.ql"

entity Pair {
    int x
    int y

    Pair(x, y)
}

Pair[] pairs = [new Pair(1, 2), new Pair(10, 20)]
int[] nums = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

print("nums = " + nums)
print("pairs = " + pairs)

print(nums[1])
print(pairs[0])

nums[3] = nums[0]

print(nums)

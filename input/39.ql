entity Cat {
    string name
    int age
    string[] interests
    
    Cat(name, age, interests)
    
    fun printInterests() {
        for (interest : interests) {
            breakpoint()
            printf("Interest: %s", [interest])
        }
    }
}

Cat beer = new Cat("Beer", 1, ["spring", "sleeping", "eating"])

breakpoint(false)

print(beer)
print("Printing interests...")

beer.printInterests()

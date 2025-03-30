entity Cat {
    string name
    int age
    string[] interests
    
    Cat(name, age, interests)
    
    fun printInterests() {
        for (interest : interests) {
            printf("Interest: %s", [interest])
        }
    }
}

Cat beer = new Cat("Beer", 1, ["spring", "sleeping", "eating"])

breakpoint()

print(beer)

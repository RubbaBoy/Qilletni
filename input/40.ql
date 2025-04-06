fun one() {
    print("One!")
    
    two()
}

fun two() {
    print("Two!")

    three()
}

fun three() {
    print("Three!")
    
    int i = 1
    
    breakpoint()
    
    printf("i: %d", [i])
}

one()

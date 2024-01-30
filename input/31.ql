entity Foo {
    int _i
    
    Foo(_i)
    
    fun getI() {
        return _i
    }
}

Foo foo = new Foo(100)
print(foo.getI())

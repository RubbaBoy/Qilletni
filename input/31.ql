import "!spotify:play_redirect.ql"
import "!spotify:playlist_tools.ql"

entity Foo {
    int _i
    
    Foo(_i)
    
    fun getI() {
        return _i
    }
}

Foo foo = new Foo(100)
print(foo.getI())

int i = foo.getI()
print("i = " + i)

collection myPlaylist = createPlaylist("test10")
print(myPlaylist)

play 
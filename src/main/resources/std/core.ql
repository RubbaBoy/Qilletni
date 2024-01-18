import "string.ql"
import "math.ql"
import "util/list.ql"
import "util/map.ql"
import "types/song.ql"
import "types/album.ql"
import "types/artist.ql"

// Prints a string representation of the given object to the console
// obj may be of any type
native fun print(obj)

// Gets the environment variable of a given name. This will return a string, or throw an error if it can't be found.
native fun getEnv(name)

// Checks if an environment variable is set
native fun hasEnv(name)

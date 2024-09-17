import "types/types.ql"
import "util/util.ql"
import "string.ql"

/**
 * Prints a string representation of the given object to the console
 * obj may be of any type
 *
 * @param obj The object to print, of any type.
 */
native fun print(obj)

/**
 * Gets the environment variable of a given name. This will return a string, or throw an error if it can't be found.
 *
 * @param[@type string] name The name of the environment variable to get
 * @returns The value of the environment variable
 */
native fun getEnv(name)

/**
 * Checks if an environment variable is set.
 *
 * @param[@type string] name The name of the environment variable to check for
 * @returns[@type boolean] true if the environment variable is set, false if otherwise
 */
native fun hasEnv(name)

/**
 * Gets the current time in milliseconds
 *
 * @returns[@type int] The current time in milliseconds
 */
native fun currentTimeMillis()

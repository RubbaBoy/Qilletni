// This file is only accessible in the debug REPL

/**
 * Prints out the functions in the current scope and all parents.
 */
native fun functions()

/**
 * Prints out the functions in the given entity's scope and all parents.
 *
 * @param entityInstance The instance of the entity to get the scope of
 */
native fun functions(entityInstance)

/**
 * Prints out the variables and their values in the current scope and all parents.
 */
native fun vars()

/**
 * Prints out the variables and their values in the given entity's scope and all parents.
 *
 * @param entityInstance The instance of the entity to get the scope of
 */
native fun vars(entityInstance)

// This could be a call to backtrace() not natively,
// however that makes the stack trace look messier
/**
 * Prints out the current stacktrace of the program.
 */
native fun bt()

/**
 * Prints out the current stacktrace of the program.
 */
native fun backtrace()

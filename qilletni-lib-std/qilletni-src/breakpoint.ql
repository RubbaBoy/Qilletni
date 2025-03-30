/**
 * Pauses the program and puts the user in a debugger.
 *
 * @returns[@type int] The number of milliseconds spent in the debugger
 */
native fun breakpoint()

/**
 * Pauses the program and puts the user in a debugger if the given condition is `true`.
 *
 * @returns[@type int] The number of milliseconds spent in the debugger, or -1 if the condition is `false`
 */
native fun breakpoint(condition)

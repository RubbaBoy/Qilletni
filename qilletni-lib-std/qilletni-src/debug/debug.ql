// This file is only accessible in the debug REPL

native fun functions()

native fun functions(entityInstance)

native fun vars()

native fun vars(entityInstance)

// This could be a call to backtrace() not natively,
// however that makes the stack trace look messier
native fun bt()

native fun backtrace()

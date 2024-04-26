// Gets the length of the string
native fun length() on string

// Checks if the string contains the given string
native fun contains(comparing) on string

// Returns a substring of the current string from the given index
native fun substring(beginIndex) on string

// Returns a substring of the current string from and to specified indices
native fun substring(beginIndex, endIndex) on string

// Returns the string in all uppercase
native fun toUpper() on string

// Returns the string in all lowercase
native fun toLower() on string

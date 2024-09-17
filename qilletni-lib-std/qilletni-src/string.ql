/**
 * Gets the length of a string.
 *
 * @returns[@type int] The length of the string
 */
native fun length() on string

/**
 * Checks if the string contains the given string.
 *
 * @param[@type string] comparing The needle
 * @returns[@type boolean] If the current string contains the given string 
 */
native fun contains(comparing) on string

/**
 * Returns a substring of the current string from the given index.
 * 
 * @param[@type int] beginIndex The index to start the substring
 * @returns[@type string] The substring
 */
native fun substring(beginIndex) on string

/**
 * Returns a substring of the current string from and to specified indices
 *
 * @param[@type int] beginIndex The index to start the substring
 * @param[@type int] endIndex The index to end the substring
 * @returns[@type string] The substring
 */
native fun substring(beginIndex, endIndex) on string

/**
 * Returns the string in all uppercase.
 *
 * @returns[@type string] The string in all uppercase
 */
native fun toUpper() on string

/**
 * Returns the string in all lowercase.
 *
 * @returns[@type string] The string in all lowercase
 */
native fun toLower() on string

/**
 * Performs typical C-style string formatting. Only strings, doubles, and integers are supported.
 * 
 * @param formatList The list of items to format with
 * @returns[@type string] The formatted string
 */
native fun format(formatList) on string

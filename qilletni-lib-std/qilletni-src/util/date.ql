/**
 * An entity that may store a date (not time).
 */
entity Date {

    /**
     * The internal [@java java.time.LocalDate] object to store the date info.
     * @type @java java.util.HashMap
     */
    java _date
    
    /**
     * Creates a new date with the given [@param date].
     */
    Date(_date)
    
    /**
     * Creates a new date from a DD/MM/YYYY formatted string.
     *
     * @param[@type string] str The string to parse
     * @returns[@type std.Date] The date
     */
    native static fun parse(str)
    
    /**
     * Gets a new date from the current time.
     *
     * @returns[@type std.Date] The date
     */
    native static fun now()
    
    /**
     * Gives the day of the month, as a number starting at 1.
     *
     * @returns[@type int] The day of the month
     */
    native fun getDay()
    
    /**
     * Gets the name of the day.
     *
     * @returns[@type string] The name of the day
     */
    native fun getDayName()
    
    /**
     * Gives the month of the year, as a number between 1-12.
     *
     * @returns[@type int] The month of the year
     */
    native fun getMonth()
    
    /**
     * Gets the name of the month.
     *
     * @returns[@type string] The name of the month
     */
    native fun getMonthName()
    
    /**
     * Gives the year.
     *
     * @returns[@type int] The year
     */
    native fun getYear()
}

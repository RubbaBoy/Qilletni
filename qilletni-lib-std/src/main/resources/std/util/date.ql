// Gets a new date from a DD/MM/YYYY formatted string
native fun newDateFrom(str)

// Gets a new date at the current time
native fun newDateNow()

entity Date {
    // Java's LocalDate type
    java _date
    
    Date(_date)
    
    // Gives the day of the month, as a number starting at 1
    native fun getDay()
    
    // Gets the name of the day
    native fun getDayName()
    
    // Gives the month of the year, as a number between 1-12
    native fun getMonth()
    
    // Gets the name of the month
    native fun getMonthName()
    
    // Gives the year
    native fun getYear()
}

package is.yarr.qilletni.api.lang.stack;

/**
 * Represents a single function call or otherwise significant flow change in a Qilletni program.
 */
public interface QilletniStackTraceElement {

    /**
     * Gets the library the function call happened in.
     * 
     * @return The library name
     */
    String getLibrary();

    /**
     * Gets the .ql file the function call happened in.
     * 
     * @return The source file name
     */
    String getFileName();

    /**
     * Gets the name of the function that was called.
     * 
     * @return The function name
     */
    String getMethodName();

    /**
     * Gets the 1-indexed line in the source .ql that the function call happened in.
     * 
     * @return The 1-indexed line the call happened in
     */
    int getLine();

    /**
     * Gets the 1-indexed column in the line that the function call happened in.
     * 
     * @return The 1-indexed line the call happened in
     */
    int getColumn();

    /**
     * Gets the string representation of the stack trace element, to be displayed in a stack trace.
     * 
     * @return The string to display
     */
    String displayString();
    
}

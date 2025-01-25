package is.yarr.qilletni.api.lang.stack;

import java.util.List;

/**
 * The internal call stack of a Qilletni program. This only counts Qilletni methods, and not the Java stack trace that
 * may be populated in a native method.
 */
public interface QilletniStackTrace {

    /**
     * Adds a stack trace element to the top of the stack, signifying a Qilletni method has been invoked (or some other
     * significant flow change).
     * 
     * @param stackTraceElement The element to push, identifying the call
     */
    void pushStackTraceElement(QilletniStackTraceElement stackTraceElement);

    /**
     * Removes the top stack trace element, when a function is exited.
     */
    void popStackTraceElement();

    /**
     * Gets the current stack trace as a list of elements.
     * 
     * @return The stack trace
     */
    List<QilletniStackTraceElement> getStackTrace();

    /**
     * Puts the stack trace contents into a displayable string.
     * 
     * @return The string representing the whole stack trace
     */
    String displayStackTrace();

    /**
     * Clones the stack trace at its current point. This is used for splitting flow control, in the case of invoking
     * functions from native methods.
     * 
     * @return The cloned stack trace
     */
    QilletniStackTrace cloneStackTrace();
    
}

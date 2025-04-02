package dev.qilletni.api.lang.internal;

import java.util.function.Consumer;

/**
 * Allows for background processes to be run, or effectively async callbacks. This is done by passing this interface a
 * {@link Runnable}, which runs a condition ID. With that, the condition ID can be activated, meaning the next break in
 * expression invocation will run the runnable.
 * <br>
 * Qilletni strictly doesn't support multithreading, but native Java functions can for processing. Any async thread
 * should <b>NOT</b> use any Qilletni APIs, aside from this class or any others that are deemed safe.
 */
public interface BackgroundTaskExecutor {
    
    /**
     * A condition that is run in between expression invocation breaks when the condition ID is triggered.
     * 
     * @param callback The callback to invoke
     * @return A number representing a condition ID
     */
    int runWhenCondition(Runnable callback);
    
    /**
     * A condition that is run in between expression invocation breaks when the condition ID is triggered.
     * 
     * @param callback The callback to invoke with a given parameter. If {@link #triggerCondition(int)} is used for this
     *                 condition instead of {@link #triggerCondition(int, Object)}, the trigger is ignored.
     * @return A number representing a condition ID
     */
    <T> int runWhenCondition(Consumer<T> callback);

    /**
     * Activates a condition from its ID. 
     * 
     * @param conditionId The condition ID to activate
     */
    void triggerCondition(int conditionId);

    /**
     * Activates a condition from its ID. 
     * 
     * @param conditionId The condition ID to activate
     * @param param The parameter given to the callback consumer
     */
    void triggerCondition(int conditionId, Object param);

    /**
     * Checks if any conditions are triggered and runs them if they are. This is mainly used for internal purposes, and
     * should <b>ONLY</b> be run in the main thread.
     */
    void checkAndRunBackgroundTasks();

    /**
     * Blocks the current thread and runs background tasks immediately when they become available.
     */
    void blockAndRunBackgroundTasks();
    
}

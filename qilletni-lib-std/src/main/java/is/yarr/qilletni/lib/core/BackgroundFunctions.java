package is.yarr.qilletni.lib.core;

import is.yarr.qilletni.api.lang.internal.BackgroundTaskExecutor;

public class BackgroundFunctions {
    
    private final BackgroundTaskExecutor backgroundTaskExecutor;

    public BackgroundFunctions(BackgroundTaskExecutor backgroundTaskExecutor) {
        this.backgroundTaskExecutor = backgroundTaskExecutor;
    }

    public void processBackground() {
        backgroundTaskExecutor.blockAndRunBackgroundTasks();
    }
    
}

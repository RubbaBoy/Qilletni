package is.yarr.qilletni.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceUtility {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorServiceUtility.class);
    
    public static void shutdown(ExecutorService executorService) {
        executorService.shutdownNow();

        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                LOGGER.debug("ExecutorService did not terminate in the specified time.");
            }
        } catch (InterruptedException e) {
            LOGGER.debug("Interrupted while waiting for termination.");
            Thread.currentThread().interrupt();
        }
    }
    
}

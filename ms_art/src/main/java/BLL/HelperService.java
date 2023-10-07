package BLL;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class HelperService {
    //source: https://www.baeldung.com/java-generating-random-numbers-in-range
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    //source: https://www.baeldung.com/java-executor-wait-for-threads
    public static void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

package is.yarr.qilletni.lib.core.nativefunctions;

import java.util.concurrent.ThreadLocalRandom;

public class MathFunctions {

    /**
     * Generates a random integer between a lower bound (inclusive) and an upper bound (exclusive).
     *
     * @param min The inclusive lower bound
     * @param max The exclusive upper bound
     */
    public static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }
    
}

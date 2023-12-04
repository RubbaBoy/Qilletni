package is.yarr.qilletni;

import is.yarr.qilletni.lang.types.IntType;
import is.yarr.qilletni.lang.types.QilletniType;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementations of some native methods of Qilletni. This might be moved into a separate file later, and its native
 * Qilletni definitions definitely consolidated into a single file (once importing is working), but these are the
 * current implementations.
 */
public class InternalNative {

    /**
     * Prints a string representation of the given object.
     * 
     * @param qilletniType The object to print
     */
    public static void print(QilletniType qilletniType) {
        System.out.println(qilletniType.stringValue());
    }

    /**
     * Generates a random integer between a lower bound (inclusive) and an upper bound (exclusive).
     * 
     * @param min The inclusive lower bound
     * @param max The exclusive upper bound
     */
    public static IntType random(IntType min, IntType max) {
        return new IntType(ThreadLocalRandom.current().nextInt(min.getValue(), max.getValue()));
    }
}

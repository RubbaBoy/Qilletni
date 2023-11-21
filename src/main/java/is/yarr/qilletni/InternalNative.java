package is.yarr.qilletni;

import is.yarr.qilletni.types.BooleanType;
import is.yarr.qilletni.types.CollectionType;
import is.yarr.qilletni.types.FunctionType;
import is.yarr.qilletni.types.IntType;
import is.yarr.qilletni.types.QilletniType;
import is.yarr.qilletni.types.SongType;
import is.yarr.qilletni.types.StringType;
import is.yarr.qilletni.types.WeightsType;

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
        var obj = switch (qilletniType) {
            case StringType s -> s.getValue();
            case BooleanType booleanType -> booleanType.getValue();
            case CollectionType collectionType -> "~collection~";
            case FunctionType functionType -> String.format("%s%s(%s)",
                    functionType.isNative() ? "native " : "",
                    functionType.getName(),
                    String.join(",", functionType.getParams()));
            case IntType intType -> intType.getValue();
            case SongType songType -> "~song~";
            case WeightsType weightsType -> "~weights~";
        };
        
        System.out.println(obj);
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

package is.yarr.qilletni.lib.core.nativefunctions;

import is.yarr.qilletni.lang.types.QilletniType;
import is.yarr.qilletni.lang.types.StringType;

public class IOFunctions {

    /**
     * Prints a string representation of the given object.
     *
     * @param qilletniType The object to print
     */
    public static void print(QilletniType qilletniType) {
        System.out.println(qilletniType.stringValue());
    }
    
    public static String add(int a, int b) {
        return String.valueOf(a + b);
    }

}

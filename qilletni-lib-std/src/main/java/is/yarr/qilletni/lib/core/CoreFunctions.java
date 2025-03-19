package is.yarr.qilletni.lib.core;

import is.yarr.qilletni.api.lang.types.QilletniType;

public class CoreFunctions {

    /**
     * Prints a string representation of the given object.
     *
     * @param qilletniType The object to print
     */
    public static void print(QilletniType qilletniType) {
        System.out.println(qilletniType.stringValue());
    }
    
    public static String readLine() {
        return System.console().readLine();
    }
    
    public static String getEnv(String name) {
        return System.getenv(name);
    }
    
    public static boolean hasEnv(String name) {
        return System.getenv(name) != null;
    }
    
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }
    
    public static void exit(int status) {
        System.exit(status);
    }
}

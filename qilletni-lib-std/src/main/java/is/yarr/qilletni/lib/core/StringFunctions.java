package is.yarr.qilletni.lib.core;

import is.yarr.qilletni.api.lib.NativeOn;

@NativeOn("string")
public class StringFunctions {

    public static int length(String string) {
        return string.length();
    }

    public static boolean contains(String string, String comparing) {
        return string.contains(comparing);
    }

    public static String substring(String string, int beginIndex) {
        return string.substring(beginIndex);
    }

    public static String substring(String string, int beginIndex, int endIndex) {
        return string.substring(beginIndex, endIndex);
    }

    public static String toUpper(String string) {
        return string.toUpperCase();
    }

    public static String toLower(String string) {
        return string.toLowerCase();
    }

}

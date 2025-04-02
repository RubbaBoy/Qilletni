package dev.qilletni.lib.core;


import dev.qilletni.api.lang.types.DoubleType;
import dev.qilletni.api.lang.types.IntType;
import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.lang.types.StringType;
import dev.qilletni.api.lib.annotations.NativeOn;

import java.util.Arrays;
import java.util.List;

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
    
    public static String format(String string, List<QilletniType> formatList) {
        var formatArray = formatList.stream().map(type -> switch (type) {
                case StringType stringType -> stringType.getValue();
                case IntType intType -> intType.getValue();
                case DoubleType doubleType -> doubleType.getValue();
                default -> type.stringValue();
            }).toArray();

//        System.out.printf("formatArray = %s, types = %s%n", Arrays.toString(formatArray), Arrays.toString(Arrays.stream(formatArray).map(Object::getClass).map(Class::getSimpleName).toArray()));
        
        return String.format(string, formatArray);
    }
    
    public static int toInt(String string) {
        return Integer.parseInt(string);
    }

}

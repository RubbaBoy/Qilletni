package is.yarr.qilletni.lang.internal;

import is.yarr.qilletni.lang.types.BooleanType;
import is.yarr.qilletni.lang.types.IntType;
import is.yarr.qilletni.lang.types.StringType;

public class StringNativeFunctions {
    
    @NativeOn("string")
    public static IntType length(StringType string) {
        return new IntType(string.getValue().length());
    }
    
    @NativeOn("string")
    public static BooleanType contains(StringType string, StringType comparing) {
        return new BooleanType(string.getValue().contains(comparing.getValue()));
    }
    
    @NativeOn("string")
    public static StringType substring(StringType string, IntType beginIndex) {
        return new StringType(string.getValue().substring(beginIndex.getValue()));
    }

    @NativeOn("string")
    public static StringType substring(StringType string, IntType beginIndex, IntType endIndex) {
        return new StringType(string.getValue().substring(beginIndex.getValue(), endIndex.getValue()));
    }
    
    @NativeOn("string")
    public static StringType toUpper(StringType string) {
        return new StringType(string.getValue().toUpperCase());
    }
    
    @NativeOn("string")
    public static StringType toLower(StringType string) {
        return new StringType(string.getValue().toLowerCase());
    }
    
}

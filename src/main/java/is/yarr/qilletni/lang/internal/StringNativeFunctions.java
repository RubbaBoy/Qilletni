package is.yarr.qilletni.lang.internal;

import is.yarr.qilletni.lang.types.IntType;
import is.yarr.qilletni.lang.types.StringType;

public class StringNativeFunctions {
    
    @NativeOn("string")
    public static IntType length(StringType string) {
        return new IntType(string.getValue().length());
    }
    
}

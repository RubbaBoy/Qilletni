package is.yarr.qilletni.lib;

import is.yarr.qilletni.lib.core.nativefunctions.IOFunctions;
import is.yarr.qilletni.lib.core.nativefunctions.ListFunctions;
import is.yarr.qilletni.lib.core.nativefunctions.MapFunctions;
import is.yarr.qilletni.lib.core.nativefunctions.MathFunctions;
import is.yarr.qilletni.lib.core.nativefunctions.StringFunctions;
import is.yarr.qilletni.lang.internal.NativeFunctionHandler;

public class LibraryInit {

    public static void registerFunctions(NativeFunctionHandler nativeFunctionHandler) {
        nativeFunctionHandler.registerClasses(MathFunctions.class, IOFunctions.class,
                StringFunctions.class, ListFunctions.class, MapFunctions.class);
    }

}

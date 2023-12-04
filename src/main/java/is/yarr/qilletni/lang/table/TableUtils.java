package is.yarr.qilletni.lang.table;

import is.yarr.qilletni.lang.exceptions.TypeMismatchException;
import is.yarr.qilletni.lang.exceptions.VariableNotFoundException;
import is.yarr.qilletni.lang.types.QilletniType;

public class TableUtils {
    
    public static void requireSymbolNotNull(Symbol<?> symbol, String name) {
        if (symbol == null) {
            throw new VariableNotFoundException("Symbol " + name + " not found!");
        }
    }
    
    public static void requireSameType(Symbol<?> symbol, QilletniType qilletniType) {
        var type1 = symbol.getType().getInternalType();
        var type2 = qilletniType.getClass();
        if (type1.equals(type2)) {
            throw new TypeMismatchException("Mismatching types: " + type1 + " and " + type2);
        }
    }
    
}

package is.yarr.qilletni.lang.table;

import is.yarr.qilletni.api.lang.table.Symbol;
import is.yarr.qilletni.lang.exceptions.TypeMismatchException;
import is.yarr.qilletni.lang.exceptions.VariableNotFoundException;
import is.yarr.qilletni.api.lang.types.QilletniType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TableUtils.class);
    
    public static void requireSymbolNotNull(Symbol<?> symbol, String name) {
        if (symbol == null) {
            throw new VariableNotFoundException("Symbol " + name + " not found!");
        }
    }
    
    public static void requireSameType(Symbol<?> symbol, QilletniType qilletniType) {
        var type1 = symbol.getType();
        var type2 = qilletniType.getTypeClass();
        LOGGER.debug("{} == {}", type1, type2);
        if (!type1.equals(type2)) {
            throw new TypeMismatchException("Mismatching types: " + type1.getTypeName() + " and " + type2.getTypeName());
        }
    }
    
}

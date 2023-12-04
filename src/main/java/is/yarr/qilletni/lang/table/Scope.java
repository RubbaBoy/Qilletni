package is.yarr.qilletni.lang.table;

import is.yarr.qilletni.lang.exceptions.AlreadyDefinedException;
import is.yarr.qilletni.lang.types.QilletniType;

import java.util.HashMap;
import java.util.Map;

public class Scope {

    private final Map<String, Symbol<?>> symbolTable = new HashMap<>();
    private final Scope parent;
    
    public Scope() {
        this.parent = null;
    }

    public Scope(Scope parent) {
        this.parent = parent;
    }

    public <T extends QilletniType> Symbol<T> lookup(String name) {
        if (parent != null && parent.isDefined(name)) {
            return parent.lookup(name);
        }
        
        var symbol = (Symbol<T>) symbolTable.get(name);
        TableUtils.requireSymbolNotNull(symbol, name);
        return symbol;
    }
    
    public boolean isDefined(String name) {
        if (parent != null && parent.isDefined(name)) {
            return true;
        }
        
        return symbolTable.containsKey(name);
    }

    public <T extends QilletniType> void define(Symbol<T> symbol) {
        if (isDefined(symbol.getName())) {
            throw new AlreadyDefinedException("Symbol " + symbol.getName() + " has already been defined!");
        }
        
        symbolTable.put(symbol.getName(), symbol);
    }

//    @Override
//    public String toString() {
//        var stringBuilder = new StringBuilder("Scope[");
//        var arr = symbolTable.values().toArray(Symbol[]::new);
//        for (int i = 0; i < arr.length; i++) {
//            stringBuilder.append(arr[i].getName()).append(" = ").append(arr[i].getValue());
//            if (i != arr.length - 1) {
//                stringBuilder.append(", ");
//            }
//        }
//        return stringBuilder + "]";
//    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder("Scope[");
        var arr = symbolTable.values().toArray(Symbol[]::new);
        for (int i = 0; i < arr.length; i++) {
            stringBuilder.append(arr[i].getName()).append(" = ").append(arr[i].getValue().stringValue());
            if (i != arr.length - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder + "]";
    }
}

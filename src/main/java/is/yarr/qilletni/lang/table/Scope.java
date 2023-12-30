package is.yarr.qilletni.lang.table;

import is.yarr.qilletni.lang.exceptions.AlreadyDefinedException;
import is.yarr.qilletni.lang.exceptions.VariableNotFoundException;
import is.yarr.qilletni.lang.types.FunctionType;
import is.yarr.qilletni.lang.types.QilletniType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Scope {

    // List<Symbol<?>> because of method overloading
    private final Map<String, Symbol<?>> symbolTable = new HashMap<>();
    private final Map<String, List<Symbol<FunctionType>>> functionSymbolTable = new HashMap<>();
    
    // Names of entities defined (not instances of entities)
//    private final Map<String, EntityType> entityTypes = new HashMap<>();
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
        if (symbol == null) {
            symbol = (Symbol<T>) functionSymbolTable.get(name).get(0);
        }
        
        TableUtils.requireSymbolNotNull(symbol, name);
        return symbol;
    }

    public Symbol<FunctionType> lookupFunction(String name, int params) {
        if (parent != null && parent.isFunctionDefined(name)) {
            return parent.lookupFunction(name, params);
        }
        
        var symbols = lookupFunction(name);
        return symbols.stream().filter(symbol -> {
            var callingParamCount = symbol.getParamCount();
            if (symbol.getValue().getOnType() != null) {
                callingParamCount--;
            }
            return callingParamCount == params;
        })
                .findFirst()
                .orElseThrow(() -> new VariableNotFoundException("Function " + name + " with " + params + " not found"));
    }

    public List<Symbol<FunctionType>> lookupFunction(String name) {
        if (parent != null && parent.isFunctionDefined(name)) {
            return parent.lookupFunction(name);
        }

        var functions = functionSymbolTable.get(name);
        if (functions == null) {
            throw new VariableNotFoundException("Function " + name + " not found!");
        }
        
        return functions;
    }
    
    public boolean isDefined(String name) {
        if (parent != null && parent.isDefined(name)) {
            return true;
        }
        
        return symbolTable.containsKey(name) || functionSymbolTable.containsKey(name);
    }
    
    public boolean isFunctionDefined(String name) {
        if (parent != null && parent.isFunctionDefined(name)) {
            return true;
        }
        
        return functionSymbolTable.containsKey(name);
    }
    
//    public boolean isEntityDefinitionDefined(String name) {
//        if (parent != null && parent.isEntityDefinitionDefined(name)) {
//            return true;
//        }
//
//        return entityTypes.containsKey(name);
//    }

    public <T extends QilletniType> void define(Symbol<T> symbol) {
        if (isDefined(symbol.getName())) {
            throw new AlreadyDefinedException("Symbol " + symbol.getName() + " has already been defined!");
        }
        
        symbolTable.put(symbol.getName(), symbol);
    }
    
    public void defineFunction(Symbol<FunctionType> functionSymbol) {
        if (isFunctionDefined(functionSymbol.getName())) {
            var functions = lookupFunction(functionSymbol.getName());
            var targetParamCount = functionSymbol.getValue().getParams().length;
            if (functions.stream().anyMatch(symbol -> symbol.getParamCount() == targetParamCount)) {
                throw new AlreadyDefinedException("Function " + functionSymbol.getName() + " has already been defined!");
            }
        }
        
        functionSymbolTable.compute(functionSymbol.getName(), (k, v) -> {
            if (v == null) {
                return List.of(functionSymbol);
            }
            
            return Stream.concat(Stream.of(functionSymbol), v.stream()).toList();
        });
    }

    public Map<String, Symbol<?>> getAllSymbols() {
        return symbolTable;
    }

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

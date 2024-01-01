package is.yarr.qilletni.lang.table;

import is.yarr.qilletni.lang.exceptions.AlreadyDefinedException;
import is.yarr.qilletni.lang.exceptions.VariableNotFoundException;
import is.yarr.qilletni.lang.types.FunctionType;
import is.yarr.qilletni.lang.types.QilletniType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Scope {
    
    private static int scopeCount = 0;

    // List<Symbol<?>> because of method overloading
    private final Map<String, Symbol<?>> symbolTable = new HashMap<>();
    private final Map<String, List<Symbol<FunctionType>>> functionSymbolTable = new HashMap<>();

    private final Scope parent;
    private final int scopeId;

    public Scope() {
        this.parent = null;
        this.scopeId = scopeCount++;
    }

    public Scope(Scope parent) {
        this.parent = parent;
        this.scopeId = scopeCount++;
    }

    public <T extends QilletniType> Symbol<T> lookup(String name) {
        if (isDirectlyDefined(name)) {
            var symbol = (Symbol<T>) symbolTable.get(name);
            if (symbol == null) {
                var functions = functionSymbolTable.get(name);
                if (functions != null && !functions.isEmpty()) {
                    symbol = (Symbol<T>) functions.get(0);
                }
            }

            return symbol;
        }
        
        if (parent != null && parent.isDefined(name)) {
            return parent.lookup(name);
        }

        throw new VariableNotFoundException("Symbol " + name + " not found!");
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
                .orElseThrow(() -> new VariableNotFoundException("Function " + name + " with " + params + " params not found"));
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

    /**
     * Checks if a symbol is defined in the current scope, not any parent. Used for shadowing variables.
     *
     * @param name The name of the symbol to look up
     * @return If the symbol is defined in the current scope
     */
    public boolean isDirectlyDefined(String name) {
        return symbolTable.containsKey(name) || functionSymbolTable.containsKey(name);
    }

    public boolean isFunctionDefined(String name) {
        if (parent != null && parent.isFunctionDefined(name)) {
            return true;
        }

        return functionSymbolTable.containsKey(name);
    }

    public <T extends QilletniType> void define(Symbol<T> symbol) {
        if (isDirectlyDefined(symbol.getName())) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scope scope = (Scope) o;
        return scopeId == scope.scopeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scopeId);
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder("Scope(" + scopeId + ")[");
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

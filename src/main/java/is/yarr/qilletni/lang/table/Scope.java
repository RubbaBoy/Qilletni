package is.yarr.qilletni.lang.table;

import is.yarr.qilletni.lang.exceptions.AlreadyDefinedException;
import is.yarr.qilletni.lang.exceptions.VariableNotFoundException;
import is.yarr.qilletni.lang.types.FunctionType;
import is.yarr.qilletni.lang.types.QilletniType;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Scope {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Scope.class);

    private static int scopeCount = 0;

    // List<Symbol<?>> because of method overloading
    private final Map<String, List<Symbol<FunctionType>>> functionSymbolTable = new HashMap<>();
    private final Map<String, Symbol<?>> symbolTable = new HashMap<>();

    private final ScopeType scopeType;
    private final Scope parent;
    private final int scopeId;

    public Scope() {
        this.scopeType = ScopeType.GLOBAL;
        this.parent = null;
        this.scopeId = scopeCount++;
    }

    public Scope(Scope parent) {
        this.scopeType = ScopeType.LOCAL;
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

        var dontLookupVar = name.startsWith("_") && parent != null &&
                (parent.scopeType == ScopeType.GLOBAL && parent.isDefined(name));
        
        if (!dontLookupVar) {
            return parent.lookup(name);
        }

        throw new VariableNotFoundException("Symbol " + name + " not found!");
    }

    public Symbol<FunctionType> lookupFunction(String name, int params) {
        return lookupFunction(name, params, null);
    }

    public Symbol<FunctionType> lookupFunction(String name, int params, QilletniTypeClass<?> onType) {
        return lookupFunctionOptionally(name, params, onType)
                .orElseThrow(() -> new VariableNotFoundException(String.format("Function %s%s with %d params not found", name, onType != null ? " on " + onType.getTypeName() : "", params)));
    }

    public Optional<Symbol<FunctionType>> lookupFunctionOptionally(String name, int params, QilletniTypeClass<?> onType) {
        LOGGER.debug("Looking up {}({}) on {}", name, params, onType);
        if (parent != null && parent.isFunctionDefined(name)) {
            LOGGER.debug("Checking in parent");
            var foundParentOptional = parent.lookupFunctionOptionally(name, params, onType);
            if (foundParentOptional.isPresent()) {
                return foundParentOptional;
            }
        }

        var symbols = lookupFunction(name);
        return symbols.stream().filter(symbol -> {
                    if (!Objects.equals(symbol.getValue().getOnType(), onType)) {
                        return false;
                    }

                    if (onType != null) {
                        LOGGER.debug("{} == {}", onType, symbol.getValue().getOnType());
                    }
                    
                    return symbol.getValue().getInvokingParamCount() == params;
                })
                .findFirst();
    }

    public List<Symbol<FunctionType>> lookupFunction(String name) {
        var allFunctions = new ArrayList<Symbol<FunctionType>>();
        if (parent != null && parent.isFunctionDefined(name)) {
            allFunctions.addAll(parent.lookupFunction(name));
        }

        allFunctions.addAll(functionSymbolTable.getOrDefault(name, Collections.emptyList()));
        if (allFunctions.isEmpty()) {
            throw new VariableNotFoundException("Function " + name + " not found!");
        }

        return allFunctions;
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
        if (parent != null && parent.scopeType == ScopeType.GLOBAL && !symbol.getName().startsWith("_")) {
            parent.define(symbol);
            return;
        }
        
        if (isDirectlyDefined(symbol.getName())) {
            throw new AlreadyDefinedException("Symbol " + symbol.getName() + " has already been defined!");
        }

        symbolTable.put(symbol.getName(), symbol);
    }

    public void defineFunction(Symbol<FunctionType> functionSymbol) {
        if (parent != null && parent.scopeType == ScopeType.GLOBAL &&
                !functionSymbol.getName().startsWith("_") &&
                // If not on an entity (should be in its own scope)
                (functionSymbol.getValue().getOnType() == null || functionSymbol.getValue().getOnType().isNativeType())) {
            parent.defineFunction(functionSymbol);
            return;
        }
        
        if (isFunctionDefined(functionSymbol.getName())) {
            var functions = lookupFunction(functionSymbol.getName());
            var targetParamCount = functionSymbol.getValue().getInvokingParamCount();
            var targetOnType = functionSymbol.getValue().getOnType();
            if (functions.stream().anyMatch(symbol -> symbol.getValue().getInvokingParamCount() == targetParamCount && Objects.equals(symbol.getValue().getOnType(), targetOnType))) {
                throw new AlreadyDefinedException(String.format("Function %s on %s has already been defined!", functionSymbol.getName(), targetOnType));
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

        stringBuilder.append(" | ");

        var arr2 = functionSymbolTable.keySet().toArray(String[]::new);
        for (int i = 0; i < arr2.length; i++) {
            var val = functionSymbolTable.get(arr2[i]);
            stringBuilder.append(arr2[i]).append(" = [").append(val.stream().map(Symbol::getValue).map(FunctionType::toString).collect(Collectors.joining(", "))).append("]");
            if (i != arr2.length - 1) {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder + "]";
    }
    
    enum ScopeType {
        GLOBAL,
        LOCAL
    }
}

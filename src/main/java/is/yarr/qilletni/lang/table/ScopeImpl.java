package is.yarr.qilletni.lang.table;

import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.api.lang.table.Symbol;
import is.yarr.qilletni.lang.exceptions.AlreadyDefinedException;
import is.yarr.qilletni.lang.exceptions.VariableNotFoundException;
import is.yarr.qilletni.api.lang.types.FunctionType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
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
import java.util.stream.Stream;

public class ScopeImpl implements Scope {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ScopeImpl.class);

    private static int scopeCount = 0;

    // List<Symbol<?>> because of method overloading
    private final Map<String, List<Symbol<FunctionType>>> functionSymbolTable = new HashMap<>();
    private final Map<String, Symbol<?>> symbolTable = new HashMap<>();

    private final ScopeType scopeType;
    private final Scope parent;
    private final int scopeId;
    private String debugDesc = "";

    public ScopeImpl() {
        this.scopeType = ScopeType.GLOBAL;
        this.parent = null;
        this.scopeId = scopeCount++;
    }

    public ScopeImpl(String debugDesc) {
        this.scopeType = ScopeType.GLOBAL;
        this.parent = null;
        this.scopeId = scopeCount++;
        this.debugDesc = debugDesc;
    }

    public ScopeImpl(Scope parent) {
        this(parent, ScopeType.LOCAL, "");
    }

    public ScopeImpl(Scope parent, ScopeType scopeType, String debugDesc) {
        this.scopeType = scopeType;
        this.parent = parent;
        this.scopeId = scopeCount++;
        this.debugDesc = debugDesc;
    }

    @Override
    public <T extends QilletniType> Symbol<T> lookup(String name) {
        if (isDirectlyDefined(name)) {
            LOGGER.debug("Directly defined: {}", name);
            var symbol = (Symbol<T>) symbolTable.get(name);
            if (symbol == null) {
                var functions = functionSymbolTable.get(name);
                if (functions != null && !functions.isEmpty()) {
                    symbol = (Symbol<T>) functions.get(0);
                }
            }

            return symbol;
        }

        var checkParentForVar = false;

        if (name.startsWith("_")) {
            checkParentForVar = parent != null && parent.getScopeType() != ScopeType.GLOBAL && parent.isDefined(name);
            LOGGER.debug("{} && {} && {}", parent == null ? "" : parent.getAllSymbols().keySet(), parent == null ? "" : parent.getScopeType() != ScopeType.GLOBAL, parent == null ? "" : parent.isDefined(name));
        } else {
            checkParentForVar = parent != null;
        }

//        if (name.startsWith("_")) {
//            if (parent != null) {
//                checkParentForVar = parent.getScopeType() != ScopeType.GLOBAL && parent.isDefined(name);
//            } else {
//                checkParentForVar = false;
//            }
//        } else {
//            checkParentForVar = parent != null;
//        }
//
//        var checkParentForVar = !name.startsWith("_") || 
//                (parent != null && parent.getScopeType() != ScopeType.GLOBAL && parent.isDefined(name));
        
        LOGGER.debug("checkParentForVar: {}", checkParentForVar);
//        LOGGER.debug("var {} checking parent? {} parent = {}", name, checkParentForVar, parent.getAllSymbols().keySet());
        LOGGER.debug("in this: {}", this);
        
        
        if (checkParentForVar) {
            LOGGER.debug("looking in parent!");
            return parent.lookup(name);
        }

        throw new VariableNotFoundException("Symbol " + name + " not found!");
    }

    @Override
    public Symbol<FunctionType> lookupFunction(String name, int params) {
        return lookupFunction(name, params, null);
    }

    @Override
    public Symbol<FunctionType> lookupFunction(String name, int params, QilletniTypeClass<?> onType) {
        return lookupFunctionOptionally(name, params, onType)
                .orElseThrow(() -> new VariableNotFoundException(String.format("Function %s%s with %d params not found", name, onType != null ? " on " + onType.getTypeName() : "", params)));
    }

    @Override
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

    @Override
    public List<Symbol<FunctionType>> lookupFunction(String name) {
        if (symbolTable.containsKey(name)) {
            var symbol = symbolTable.get(name);
            if (symbol.getType().equals(QilletniTypeClass.FUNCTION)) {
                return List.of((Symbol<FunctionType>) symbol);
            }
        }
        
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

    @Override
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

    @Override
    public boolean isFunctionDefined(String name) {
        if (parent != null && parent.isFunctionDefined(name)) {
            return true;
        }

        if (functionSymbolTable.containsKey(name)) {
            return true;
        }
        
        if (symbolTable.containsKey(name) && symbolTable.get(name).getType().equals(QilletniTypeClass.FUNCTION)) {
            return true;
        }
        
        return false;
    }

    @Override
    public <T extends QilletniType> void define(Symbol<T> symbol) {
//        if (parent != null && parent.getScopeType() == ScopeType.GLOBAL && !symbol.getName().startsWith("_")) {
//            parent.define(symbol);
//            return;
//        }
        
        if (isDirectlyDefined(symbol.getName())) {
            throw new AlreadyDefinedException("Symbol " + symbol.getName() + " has already been defined!");
        }

        symbolTable.put(symbol.getName(), symbol);
    }

    @Override
    public void defineFunction(Symbol<FunctionType> functionSymbol) {
        if (parent != null && parent.getScopeType() == ScopeType.GLOBAL &&
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

    @Override
    public Map<String, Symbol<?>> getAllSymbols() {
        return symbolTable;
    }

    @Override
    public ScopeType getScopeType() {
        return scopeType;
    }

    @Override
    public Scope getParent() {
        return parent;
    }

    @Override
    public String getDebugDesc() {
        return debugDesc;
    }

    @Override
    public void setDebugDesc(String desc) {
        this.debugDesc = desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScopeImpl scope)) return false;
        return scopeId == scope.scopeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scopeId);
    }

    @Override
    public String toString() {
        return "Scope(" + scopeId + ", " + (!debugDesc.isEmpty() ? (debugDesc + ", ") : "") + scopeType.name() + ", " + Arrays.toString(symbolTable.keySet().toArray()) + ", parent = " + parent + ")";
//        var stringBuilder = new StringBuilder("Scope(" + scopeId + ")[");
//        var arr = symbolTable.values().toArray(Symbol[]::new);
//        for (int i = 0; i < arr.length; i++) {
//            stringBuilder.append(arr[i].getName()).append(" = ").append(arr[i].getValue().stringValue());
//            if (i != arr.length - 1) {
//                stringBuilder.append(", ");
//            }
//        }
//
//        stringBuilder.append(" | ");
//
//        var arr2 = functionSymbolTable.keySet().toArray(String[]::new);
//        for (int i = 0; i < arr2.length; i++) {
//            var val = functionSymbolTable.get(arr2[i]);
//            stringBuilder.append(arr2[i]).append(" = [").append(val.stream().map(Symbol::getValue).map(FunctionType::toString).collect(Collectors.joining(", "))).append("]");
//            if (i != arr2.length - 1) {
//                stringBuilder.append(", ");
//            }
//        }
//
//        return stringBuilder + "]";
    }

}

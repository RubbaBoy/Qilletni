package is.yarr.qilletni.api.lang.table;

import is.yarr.qilletni.api.lang.types.FunctionType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Scope {
    <T extends QilletniType> Symbol<T> lookup(String name);

    Symbol<FunctionType> lookupFunction(String name, int params);

    Symbol<FunctionType> lookupFunction(String name, int params, QilletniTypeClass<?> onType);

    Optional<Symbol<FunctionType>> lookupFunctionOptionally(String name, int params, QilletniTypeClass<?> onType);

    List<Symbol<FunctionType>> lookupFunction(String name);

    boolean isDefined(String name);

    boolean isFunctionDefined(String name);

    <T extends QilletniType> void define(Symbol<T> symbol);

    void defineFunction(Symbol<FunctionType> functionSymbol);

    Map<String, Symbol<?>> getAllSymbols();
    
    ScopeType getScopeType();

    Scope getParent();

    enum ScopeType {
        GLOBAL,
        LOCAL
    }
}

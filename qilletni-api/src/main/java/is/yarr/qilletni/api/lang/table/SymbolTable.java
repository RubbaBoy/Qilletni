package is.yarr.qilletni.api.lang.table;

import java.util.Stack;

public interface SymbolTable {
    Scope initScope(Scope globalScope);

    /**
     * Creates a new scope as a child of the previous parent one and returns it.
     *
     * @return The new, empty scope
     */
    Scope pushScope();

    Scope swapScope(Scope newScope);

    Scope unswapScope();

    void popScope();

    Scope currentScope();

    /**
     * Resets the current scope to one that is only parents with the global one.
     *
     * @return
     */
    Scope functionCall();

    Scope endFunctionCall();

    Stack<Scope> getAllScopes();

    int getId();
}

package is.yarr.qilletni.api.lang.table;

import java.util.Stack;

/**
 * Represents a symbol table in the Qilletni type system. A symbol table is a collection of scopes, each of which
 * contains a mapping of names to {@link Symbol}, allowing the resolution and definition of symbols. The current scope
 * is represented by a stack of scopes, and may be pushed, popped, or swapped with another scope.
 */
public interface SymbolTable {

    /**
     * Initializes the symbol table with a given global scope as the parent. This method is invoked when each file is
     * parsed. The global scope is the parent of all other scopes.
     * 
     * @param globalScope The scope to set as the parent, shared by all other scopes
     * @return The created {@link Scope}
     */
    Scope initScope(Scope globalScope);

    /**
     * Creates a new scope as a child of the previous parent one and returns it.
     *
     * @return The new, empty scope
     */
    Scope pushScope();

    /**
     * Swaps the current scope with a new one, and returns the new scope. The previous scope is added to a stack, and
     * can be restored via {@link #unswapScope()}.
     * 
     * @param newScope The new scope to swap to
     * @return The new scope
     */
    Scope swapScope(Scope newScope);

    /**
     * Restores a previous scope that was swapped out via {@link #swapScope(Scope)}.
     * 
     * @return The new current scope
     */
    Scope unswapScope();

    /**
     * Pops the current scope off the stack, and sets the parent scope as the current one.
     */
    void popScope();

    /**
     * Gets the current scope.
     * 
     * @return The current scope
     */
    Scope currentScope();

    /**
     * Resets the current scope to one that is only parents with the global one, or the function definitions' parent
     * entity.
     *
     * @return The new current scope
     */
    Scope functionCall();

    /**
     * Ends a function call's scope as created via {@link #functionCall()}, bringing it back to the original.
     * 
     * @return The new current scope
     */
    Scope endFunctionCall();

    /**
     * Gets all available scopes, both used and unused.
     * 
     * @return All scopes
     */
    Stack<Scope> getAllScopes();

    /**
     * Gets the unique ID of the {@link SymbolTable}, generally for debugging purposes.
     * 
     * @return The ID of the symbol table
     */
    int getId();
}

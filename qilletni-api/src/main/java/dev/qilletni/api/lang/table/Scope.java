package dev.qilletni.api.lang.table;

import dev.qilletni.api.lang.types.FunctionType;
import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a scope in the Qilletni type system. A scope is a mapping of names to symbols and functions, allowing the
 * resolution and definition of variables and functions within different levels of visibility, such as global, local,
 * or within specific entities. This interface provides methods for looking up symbols and functions, defining new
 * variables or functions, and retrieving metadata about the scope.
 * <br><br>
 * Scopes have parents, and depending on the condition, will look up to their parent to see if a symbol exists.
 */
public interface Scope {

    /**
     * Looks up a symbol or function in the current scope and any parents by name. If the name begins with a
     * <code>_</code> the parent scope (if any) will not be checked. If the symbol is not found, an exception is thrown.
     * To check beforehand if the variable would be found, check {@link #isDefined(String)}.
     * 
     * @param name The name of the symbol to look up
     * @return The found symbol
     * @param <T> The type of the symbol
     */
    <T extends QilletniType> Symbol<T> lookup(String name);

    /**
     * Looks up a function with a given name and number of parameters. If the function is not found, an exception is
     * thrown. To check beforehand if the variable would be found, check {@link #isFunctionDefined(String)}.
     * 
     * @param name The name of the function
     * @param params The number of parameters the function has
     * @return The found symbol
     */
    Symbol<FunctionType> lookupFunction(String name, int params);

    /**
     * Looks up a function with a given name and number of parameters, that is on a type (either defined in an entity
     * or is an extension method). If the function is not found, an exception is thrown. To check beforehand if the
     * variable would be found, check {@link #isFunctionDefined(String)}.
     *
     * @param name The name of the function
     * @param params The number of parameters the function has
     * @return The found symbol
     */
    Symbol<FunctionType> lookupFunction(String name, int params, QilletniTypeClass<?> onType);

    /**
     * Looks up a function with a given name and number of parameters. If the <code>onType</code> is not null, it will
     * look one up that is on a type (either defined in an entity or is an extension method). If the function is not
     * found, an empty optional is returned.
     *
     * @param name The name of the function
     * @param params The number of parameters the function has
     * @return The found symbol, if any
     */
    Optional<Symbol<FunctionType>> lookupFunctionOptionally(String name, int params, QilletniTypeClass<?> onType);

    /**
     * Looks up all functions with the given name. Each one will have different parameters.
     * 
     * @param name The name of the function
     * @return All found symbols, or an empty list
     */
    List<Symbol<FunctionType>> lookupFunction(String name);

    /**
     * Checks if the given symbol name is defined in this scope or any applicable parent scopes.
     * 
     * @param name The name of the symbol to look up
     * @return If the symbol is defined
     */
    boolean isDefined(String name);

    /**
     * Checks if the given symbol name is defined in this direct scope. This does not check any parents.
     * 
     * @param name The name of the symbol to look up
     * @return If the symbol is defined
     */
    boolean isDirectlyDefined(String name);

    /**
     * Checks if the given function name is defined in this scope or any applicable parent scopes.
     * 
     * @param name The name of the function to look up
     * @return If the function is defined
     */
    boolean isFunctionDefined(String name);

    /**
     * Defines the given symbol this scope. An exception will be thrown if the symbol is already defined.
     * 
     * @param symbol The symbol to define
     * @param <T> The type of the symbol
     */
    <T extends QilletniType> void define(Symbol<T> symbol);

    /**
     * Defines the given function in this scope. An exception will be thrown if the function is already defined with
     * the same signature.
     * 
     * @param functionSymbol The function to define
     */
    void defineFunction(Symbol<FunctionType> functionSymbol);

    /**
     * Gets all symbols in the current scope, as a map of names and values.
     * 
     * @return The map of symbols
     */
    Map<String, Symbol<?>> getAllSymbols();
    
    /**
     * Gets all function symbols in the current scope, as a map of names and list of matching functions with the name.
     * The value is a list because multiple functions with different argument counts can have the same name.
     * 
     * @return The map of function symbols
     */
    Map<String, List<Symbol<FunctionType>>> getAllFunctionSymbols();

    /**
     * Gets the type of the scope, meaning how it is used or defined.
     * 
     * @return The type of the scope
     */
    ScopeType getScopeType();

    /**
     * Gets the parent scope, if any. Some methods look into the parent scope to find symbols.
     * 
     * @return The parent {@link Scope}
     */
    Scope getParent();

    /**
     * Gets a unique readable name of the scope, for debugging purposes.
     * 
     * @return The identifying string, for debugging
     */
    String getDebugDesc();

    /**
     * Sets the debug description, for identification.
     * 
     * @param desc The new description
     */
    void setDebugDesc(String desc);

    /**
     * How the scope is used or defined.
     */
    enum ScopeType {
        GLOBAL,
        FUNCTION,
        LOCAL,
        ENTITY,
        FILE,
        ALIASED_GLOBAL
    }
}

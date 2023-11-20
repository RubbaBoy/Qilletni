package is.yarr.qilletni;

import is.yarr.qilletni.table.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SymbolTable {
    
    private final Stack<Scope> scopeStack = new Stack<>();
    private final List<Scope> allScopes = new ArrayList<>();
    
    public SymbolTable() {
        var globalScope = new Scope();
        scopeStack.push(globalScope);
        allScopes.add(globalScope);
    }

    /**
     * Creates a new scope as a child of the previous parent one and returns it.
     * 
     * @return The new, empty scope
     */
    public Scope pushScope() {
        var parentScope = scopeStack.peek();
        var scope = new Scope(parentScope);
        scopeStack.push(scope);
        allScopes.add(scope);
        return scope;
    }
    
    public void popScope() {
        scopeStack.pop();
    }
    
    public Scope currentScope() {
        return scopeStack.peek();
    }

    public Stack<Scope> getScopeStack() {
        return scopeStack;
    }

    public List<Scope> getAllScopes() {
        return allScopes;
    }
}

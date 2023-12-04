package is.yarr.qilletni.lang;

import is.yarr.qilletni.lang.table.Scope;

import java.util.List;
import java.util.Stack;

public class SymbolTable {
    
    private final Scope globalScope = new Scope();
    
    private Stack<Scope> currentScopeStack = new Stack<>();
    private final Stack<Stack<Scope>> previousScopeStacks = new Stack<>();
    
    public void initScope() {
        currentScopeStack.push(globalScope);
    }

    /**
     * Creates a new scope as a child of the previous parent one and returns it.
     * 
     * @return The new, empty scope
     */
    public Scope pushScope() {
        var parentScope = currentScopeStack.isEmpty() ? null : currentScopeStack.peek();
        var scope = new Scope(parentScope);
        currentScopeStack.push(scope);
        previousScopeStacks.add(currentScopeStack);
        return scope;
    }
    
    public void popScope() {
        currentScopeStack.pop();
    }
    
    public Scope currentScope() {
        return currentScopeStack.peek();
    }

    /**
     * Resets the current scope to one that is only parents with the global one.
     * 
     * @return
     */
    public Scope functionCall() {
        previousScopeStacks.push(currentScopeStack);
        
        currentScopeStack = new Stack<>();
        currentScopeStack.push(globalScope);
        currentScopeStack.push(new Scope(globalScope));

        return currentScopeStack.peek();
    }
    
    public Scope endFunctionCall() {
        currentScopeStack = previousScopeStacks.pop();

        return currentScopeStack.peek();
    }

    public Stack<Scope> getScopeStack() {
        return currentScopeStack;
    }

    public List<Stack<Scope>> getAllScopes() {
        return previousScopeStacks;
    }
}

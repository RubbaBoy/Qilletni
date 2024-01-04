package is.yarr.qilletni.lang.table;

import java.util.List;
import java.util.Stack;

public class SymbolTable {
    
    private Stack<Scope> currentScopeStack = new Stack<>();
    private final Stack<Stack<Scope>> previousScopeStacks = new Stack<>();
    
    public Scope initScope(Scope globalScope) {
        currentScopeStack.push(globalScope);
        currentScopeStack.push(new Scope(globalScope));
        return globalScope;
    }

    /**
     * Creates a new scope as a child of the previous parent one and returns it.
     * 
     * @return The new, empty scope
     */
    public Scope pushScope() {
        var parentScope = currentScopeStack.isEmpty() ? null : currentScopeStack.peek();
        var scope = new Scope(parentScope);
        return currentScopeStack.push(scope);
    }
    
    public Scope swapScope(Scope newScope) {
        previousScopeStacks.push(currentScopeStack);

        currentScopeStack = new Stack<>();
        currentScopeStack.push(newScope);

        return currentScopeStack.peek();
    }
    
    public Scope unswapScope() {
        currentScopeStack = previousScopeStacks.pop();

        return currentScopeStack.peek();
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
        var functionGlobal = currentScopeStack.firstElement();
        
        currentScopeStack = new Stack<>();
        currentScopeStack.push(functionGlobal);
        currentScopeStack.push(new Scope(functionGlobal));

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

    @Override
    public String toString() {
        return "SymbolTable{" +
                "currentScopeStack=" + currentScopeStack +
                ", previousScopeStacks=" + previousScopeStacks +
                '}';
    }
}

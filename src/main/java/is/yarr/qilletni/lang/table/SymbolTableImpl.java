package is.yarr.qilletni.lang.table;

import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.api.lang.table.SymbolTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Stack;

public class SymbolTableImpl implements SymbolTable {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SymbolTableImpl.class);
    
    private Scope currentScope = null;
    private final Stack<Scope> previousScopeStacks = new Stack<>();
    private final String debugDesc;
    private final int id;
    
    private static int idCount;
    
    public SymbolTableImpl(String debugDesc) {
        this.debugDesc = debugDesc;
        this.id = idCount++;
    }
    
    @Override
    public Scope initScope(Scope globalScope) {
//        currentScopeStack.push(globalScope);
        currentScope = new ScopeImpl(globalScope, Scope.ScopeType.LOCAL, "file local");
        return globalScope;
    }

    @Override
    public Scope pushScope() {
        currentScope = new ScopeImpl(currentScope);
        return currentScope;
//        return currentScopeStack.push(scope);
    }
    
    @Override
    public Scope swapScope(Scope newScope) {
        previousScopeStacks.push(currentScope);

        currentScope = newScope;
//        currentScope.push(newScope);

        return currentScope;
    }
    
    @Override
    public Scope unswapScope() {
        currentScope = previousScopeStacks.pop();

        return currentScope;
    }
    
    @Override
    public void popScope() {
        currentScope = currentScope.getParent();
        
        if (currentScope == null) {
            LOGGER.error("Popped scope to null!!");
        }
    }
    
    @Override
    public Scope currentScope() {
        return currentScope;
    }

    @Override
    public Scope functionCall() {
//        LOGGER.debug("Deciding what to do! Current scope");
        
        previousScopeStacks.push(currentScope);
        
        var global = currentScope.getParent();
        if (currentScope.getScopeType() == Scope.ScopeType.ENTITY) {
            global = currentScope;
        }
        
//        var global = currentScope.getParent(); // has functions in it, is GLOBAL
        
//        LOGGER.debug("setting parent as: {}", currentScopeStack.firstElement());
//        LOGGER.debug("FULL though is: {}", currentScopeStack.lastElement());
        
//        currentScopeStack = new Stack<>();
//        currentScopeStack.push(functionGlobal); // The parent where all global functions are stored
        currentScope = new ScopeImpl(global, Scope.ScopeType.FUNCTION, "Fun local"); // Local to the function, inside. Params should be defined here

        return currentScope;
    }
    
    @Override
    public Scope endFunctionCall() {
        currentScope = previousScopeStacks.pop();

        return currentScope;
    }

    @Override
    public Stack<Scope> getAllScopes() {
        return previousScopeStacks;
    }
    
    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof SymbolTableImpl that)) return false;
//        return Objects.equals(debugDesc, that.debugDesc) && Objects.equals(currentScope, that.currentScope); // && Objects.equals(previousScopeStacks, that.previousScopeStacks);
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
//        LOGGER.debug("SymbolTable hash! {} and {} (they {} and {})", Objects.hash(currentScopeStack), Objects.hash(previousScopeStacks), currentScopeStack, previousScopeStacks);
//        return Objects.hash(debugDesc, currentScope);
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SymbolTable(" + debugDesc + "){" +
                "currentScope = " + currentScope +
                ", previousScopes = " + previousScopeStacks +
                '}';
    }
}

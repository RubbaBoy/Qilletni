package is.yarr.qilletni.lib.core;

import is.yarr.qilletni.api.lang.internal.debug.DebugSupport;
import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.api.lang.table.SymbolTable;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lib.annotations.BeforeAnyInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DebugFunctions {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DebugFunctions.class);
    
    private final DebugSupport debugSupport;
    private final SymbolTable symbolTable;
    
    private boolean ignoreCall = false;

    public DebugFunctions(DebugSupport debugSupport) {
        this.debugSupport = debugSupport;
        this.symbolTable = debugSupport.getSymbolTable();
    }
    
    @BeforeAnyInvocation
    public void inBreakpointCheck() {
        if (!debugSupport.isInBreakpoint()) {
            ignoreCall = true;
            LOGGER.error("Debug functions can only be used in a breakpoint REPL");
        }
    }
    
    private void printFunctionsFromScope(Scope scope, Set<Scope> printedScopes) {
        printFunctionsFromScope(scope, 0, printedScopes);
    }
    
    private void printFunctionsFromScope(Scope scope, int depth, Set<Scope> printedScopes) {
        if (ignoreCall) return;
        
        var spaces = "  ".repeat(depth + 1);

        if (printedScopes.contains(scope)) {
            System.out.printf("%sScope %s (already printed)%n", spaces, scope.getDebugDesc());
            return;
        }

        printedScopes.add(scope);

        if (depth > 0) {
            System.out.printf("%sScope %s%n", spaces, scope.getDebugDesc());
        }
        
        scope.getAllFunctionSymbols().forEach((name, symbols) -> {
            var nonExtensionFunctions = symbols.stream()
                    .filter(symbol -> symbol.getValue().getOnType() == null) // Only non-extension functions
                    .map(symbol -> symbol.getValue().stringValue())
                    .toList();
            
            if (nonExtensionFunctions.size() == 1) {
                System.out.printf("%s%s%n", spaces, nonExtensionFunctions.getFirst());
                return;
            }
        
            if (!nonExtensionFunctions.isEmpty()) {
                System.out.printf("%s%s:%n", spaces, name);
                nonExtensionFunctions.forEach(value -> System.out.printf("  %s%s%n", spaces, value));
            }
        });
        
        if (scope.getParent() != null) {
            printFunctionsFromScope(scope.getParent(), depth + 1, printedScopes);
        }
    }
    
    private void printVariablesFromScope(Scope scope, Set<Scope> printedScopes) {
        printVariablesFromScope(scope, 0, printedScopes);
    }
    
    private void printVariablesFromScope(Scope scope, int depth, Set<Scope> printedScopes) {
        if (ignoreCall) return;
        
        var spaces = "  ".repeat(depth + 1);

        if (printedScopes.contains(scope)) {
            System.out.printf("%sScope %s (already printed)%n", spaces, scope.getDebugDesc());
            return;
        }

        printedScopes.add(scope);
        
        if (depth > 0) {
            System.out.printf("%sScope %s%n", spaces, scope.getDebugDesc());
        }
        
        if (scope.getAllSymbols().isEmpty()) {
            System.out.printf("%sNo variables in scope%n", spaces);
        }
        
        scope.getAllSymbols().forEach((name, symbol) ->
                System.out.printf("%s%s: %s%n", spaces, name, symbol.getValue().stringValue()));
        
        if (scope.getParent() != null) {
            printVariablesFromScope(scope.getParent(), depth + 1, printedScopes);
        }
    }

    public void functions() {
        if (ignoreCall) return;
        
        var currentScope = symbolTable.currentScope();
        var printedScopes = new HashSet<Scope>();

        for (var scope : symbolTable.getAllScopes()) {
            if (scope.equals(currentScope)) continue;
            
            System.out.printf("Scope %s%s%n", scope.getDebugDesc(), scope.equals(currentScope) ? " (current)" : "");
            printFunctionsFromScope(scope, printedScopes);
        }
    }

    public void functions(EntityType entityType) {
        if (ignoreCall) return;
        
        System.out.printf("Functions For %s%n", entityType.typeName());
        printFunctionsFromScope(entityType.getEntityScope(), new HashSet<>());
    }

    public void vars() {
        if (ignoreCall) return;
        
        var currentScope = symbolTable.currentScope();
        var printedScopes = new HashSet<Scope>(); 

        for (var scope : symbolTable.getAllScopes()) {
            if (scope.equals(currentScope)) continue;
            
            System.out.printf("%nScope %s%n", scope.getDebugDesc());
            
            printVariablesFromScope(scope, printedScopes);
        }
    }

    public void vars(EntityType entityType) {
        if (ignoreCall) return;
        
        System.out.printf("Variables In %s instance%n", entityType.typeName());
        printVariablesFromScope(entityType.getEntityScope(), new HashSet<>());
    }

    public void backtrace() {
        if (ignoreCall) return;
        
        debugSupport.printBacktrace();
    }
}

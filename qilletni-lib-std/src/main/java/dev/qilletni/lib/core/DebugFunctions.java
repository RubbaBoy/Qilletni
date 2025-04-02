package dev.qilletni.lib.core;

import dev.qilletni.api.lang.internal.debug.DebugSupport;
import dev.qilletni.api.lang.table.Scope;
import dev.qilletni.api.lang.table.Symbol;
import dev.qilletni.api.lang.table.SymbolTable;
import dev.qilletni.api.lang.types.EntityType;
import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.lib.annotations.BeforeAnyInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class DebugFunctions {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DebugFunctions.class);

    final String ANSI_GREEN = "\u001B[32m";
    final String ANSI_GRAY = "\u001B[90m";
    final String ANSI_RESET = "\u001B[0m";
    
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
        var currentScopeStr = scope.equals(symbolTable.currentScope()) ? " (current) " : "";
        
        var spaces = "  ".repeat(depth + 1);

        if (printedScopes.contains(scope)) {
            System.out.printf("%sScope %s %s(already printed)%n", spaces, scope.getDebugDesc(), currentScopeStr);
            return;
        }

        printedScopes.add(scope);

        if (depth > 0) {
            System.out.printf("%sScope %s%s%n", spaces, scope.getDebugDesc(), currentScopeStr);
        }

        if (scope.getAllFunctionSymbols().isEmpty()) {
            System.out.printf("%s%s - No functions in scope -%s%n", spaces, ANSI_GRAY, ANSI_RESET);
        }

        scope.getAllFunctionSymbols().forEach((name, symbols) -> {
            var nonExtensionFunctions = symbols.stream()
                    .map(Symbol::getValue)
                    .filter(functionType -> !functionType.isExternallyDefined()) // Only functions native to the scope
                    .map(QilletniType::stringValue)
                    .toList();
            
            if (nonExtensionFunctions.size() == 1) {
                System.out.printf("%s%s%s%s%n", spaces, ANSI_GREEN, nonExtensionFunctions.getFirst(), ANSI_RESET);
                return;
            }
        
            if (!nonExtensionFunctions.isEmpty()) {
                System.out.printf("%s%s:%n", spaces, name);
                nonExtensionFunctions.forEach(value -> System.out.printf("  %s%s%s%s%n", spaces, ANSI_GREEN, value, ANSI_RESET));
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
        var currentScopeStr = scope.equals(symbolTable.currentScope()) ? " (current) " : "";
        
        var spaces = "  ".repeat(depth + 1);

        if (printedScopes.contains(scope)) {
            System.out.printf("%sScope %s %s(already printed)%n", spaces, scope.getDebugDesc(), currentScopeStr);
            return;
        }

        printedScopes.add(scope);
        
        if (depth > 0) {
            System.out.printf("%sScope %s%s%n", spaces, scope.getDebugDesc(), currentScopeStr);
        }
        
        if (scope.getAllSymbols().isEmpty()) {
            System.out.printf("%s%s - No variables in scope -%s%n", spaces, ANSI_GRAY, ANSI_RESET);
        }
        
        scope.getAllSymbols().forEach((name, symbol) ->
                System.out.printf("%s%s%s%s: %s%n", spaces, ANSI_GREEN, name, ANSI_RESET, symbol.getValue().stringValue()));
        
        if (scope.getParent() != null) {
            printVariablesFromScope(scope.getParent(), depth + 1, printedScopes);
        }
    }

    public void functions() {
        if (ignoreCall) return;
        
        var currentScope = symbolTable.currentScope();
        var printedScopes = new HashSet<Scope>();

        System.out.printf("%nScope %s (current)%n", currentScope.getDebugDesc());
        printVariablesFromScope(currentScope, printedScopes);

        for (var scope : symbolTable.getAllScopes()) {
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

        System.out.printf("%nScope %s (current)%n", currentScope.getDebugDesc());
        printVariablesFromScope(currentScope, printedScopes);

        for (var scope : symbolTable.getAllScopes()) {
            System.out.printf("%nScope %s%s%n", scope.getDebugDesc(), scope.equals(currentScope) ? " (current)" : "");
            
            printVariablesFromScope(scope, printedScopes);
        }
    }

    public void vars(EntityType entityType) {
        if (ignoreCall) return;
        
        System.out.printf("Variables In %s instance%n", entityType.typeName());
        printVariablesFromScope(entityType.getEntityScope(), new HashSet<>());
    }

    public void bt() {
        backtrace();
    }

    public void backtrace() {
        if (ignoreCall) return;
        
        debugSupport.printBacktrace();
    }
}

package dev.qilletni.api.lang.internal.debug;

import dev.qilletni.api.lang.table.SymbolTable;

public interface DebugSupport {
    
    String getLastExecutedFunctionLine();
    
    int runDebugLine(String line);
    
    boolean isDebugEnabled();
    
    void printBacktrace();
    
    SymbolTable getSymbolTable();
    
    void enterBreakpoint();
    
    void exitBreakpoint();
    
    boolean isInBreakpoint();
    
}

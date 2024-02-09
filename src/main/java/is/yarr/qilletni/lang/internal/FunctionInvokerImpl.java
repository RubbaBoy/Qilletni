package is.yarr.qilletni.lang.internal;

import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.api.exceptions.QilletniException;
import is.yarr.qilletni.api.lang.internal.FunctionInvoker;
import is.yarr.qilletni.api.lang.stack.QilletniStackTrace;
import is.yarr.qilletni.api.lang.stack.QilletniStackTraceElement;
import is.yarr.qilletni.api.lang.table.SymbolTable;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.FunctionType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.lang.QilletniVisitor;
import is.yarr.qilletni.lang.exceptions.FunctionInvocationException;
import is.yarr.qilletni.lang.exceptions.InvalidParameterException;
import is.yarr.qilletni.lang.exceptions.QilletniContextException;
import is.yarr.qilletni.lang.stack.QilletniStackTraceElementImpl;
import is.yarr.qilletni.lang.stack.StackUtils;
import is.yarr.qilletni.lang.table.SymbolImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FunctionInvokerImpl implements FunctionInvoker {
    
    private final SymbolTable symbolTable;
    private final NativeFunctionHandler nativeFunctionHandler;
    private final Map<SymbolTable, QilletniVisitor> symbolTableMap;
    
    // There is a FunctionInvokerImpl for every native method call (and in QilletniVisitor, which is not copied), so this can persist from invocation
    private final QilletniStackTrace currentStackTrace;

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionInvokerImpl.class);

    public FunctionInvokerImpl(SymbolTable symbolTable, Map<SymbolTable, QilletniVisitor> symbolTableMap, NativeFunctionHandler nativeFunctionHandler) {
        this(symbolTable, symbolTableMap, nativeFunctionHandler, null);
    }

    public FunctionInvokerImpl(SymbolTable symbolTable, Map<SymbolTable, QilletniVisitor> symbolTableMap, NativeFunctionHandler nativeFunctionHandler, QilletniStackTrace qilletniStackTrace) {
        this.symbolTable = symbolTable;
        this.symbolTableMap = symbolTableMap;
        this.nativeFunctionHandler = nativeFunctionHandler;
        this.currentStackTrace = qilletniStackTrace;
    }

    @Override
    public <T extends QilletniType> Optional<T> invokeFunction(FunctionType alreadyFoundFunction, List<QilletniType> params) {
        var stackTraceElement = findCallingMethod();
        return invokeFunction(alreadyFoundFunction.getName(), params, null, alreadyFoundFunction, () -> currentStackTrace.pushStackTraceElement(stackTraceElement));
    }

    private <T extends QilletniType> Optional<T> invokeFunction(String functionName, List<QilletniType> params, QilletniType invokedOn, Runnable pushStackTrace) {
        return invokeFunction(functionName, params, invokedOn, null, pushStackTrace);
    }

    private <T extends QilletniType> Optional<T> invokeFunction(String functionName, List<QilletniType> params, QilletniType invokedOn, FunctionType alreadyFoundFunction, Runnable pushStackTrace) {
        LOGGER.debug("invokedOn = {}", invokedOn);
        var hasOnType = invokedOn != null;

        var swappedLookupScope = false;
        
        // swap lookup scope
        LOGGER.debug("({}) invokedOn = {}, and {}", functionName, invokedOn, invokedOn instanceof EntityType);
        if (invokedOn instanceof EntityType entityType) {
            LOGGER.debug("SWAP scope! to {}", entityType.getEntityScope().getAllSymbols().keySet());
            swappedLookupScope = true;
            symbolTable.swapScope(entityType.getEntityScope());
        }

        var scope = symbolTable.currentScope();
        
        LOGGER.debug("{}'s scope: {}", functionName, scope);

        var functionType = alreadyFoundFunction != null ? alreadyFoundFunction : scope.lookupFunction(functionName, params.size(), invokedOn != null ? invokedOn.getTypeClass() : null).getValue();

        LOGGER.debug("functionType = {}", functionType);
        LOGGER.debug("woar on type: {} invoked on: {}", functionType.getOnType(), invokedOn != null ? invokedOn.getTypeClass() : "-");
        
        if (hasOnType && !functionType.getOnType().equals(invokedOn.getTypeClass())) {
            throw new FunctionInvocationException("Function not to be invoked on " + invokedOn.getTypeClass() + " should be " + functionType.getOnType());
        }

        var swapInvocationScope = false;

        // If this has an on type and is native, we need to allow it to look it up in the native function handler with the on type
        // This isn't needed if it's implemented, as it has no additional type param
        if (swappedLookupScope) {
            if (functionType.isNative() || functionType.isExternallyDefined()) {
                // Return to normal scope if either native (wouldn't really make a difference but might as well) or
                // if it is externally defined, so it is invoked normally.
                symbolTable.unswapScope();
            } else {
                swapInvocationScope = true;
            }
        }

        LOGGER.debug("swapInvocationScope = !({} || {})", functionType.isNative(), functionType.isExternallyDefined());

        var functionParams = new ArrayList<>(Arrays.asList(functionType.getParams()));

        var expectedParamLength = functionType.getInvokingParamCount();

        if (expectedParamLength != params.size()) {
            throw new InvalidParameterException("Expected " + expectedParamLength + " parameters, got " + params.size() + " onType: " + hasOnType);
        }

        QilletniTypeClass<?> invokingUponExpressionType = null;
        // If there is an on type param, add it
        LOGGER.debug("{} != {}", functionType.getInvokingParamCount(), functionType.getDefinedParamCount());
        LOGGER.debug("func: {}", functionType.getName());
        if (invokedOn != null) {
            invokingUponExpressionType = invokedOn.getTypeClass();

            if (functionType.getInvokingParamCount() != functionType.getDefinedParamCount()) {
                params.add(0, invokedOn);
            }
        }
        
        var functionScope = symbolTable.functionCall();

//        LOGGER.debug("here!!! {}", functionScope.hashCode());
//        LOGGER.debug("here!!! {}", symbolTable.currentScope().hashCode());

        if (functionType.isNative()) {
            LOGGER.debug("Invoking native! {}", functionType.getName());
            LOGGER.debug("symtab = {}", symbolTable.currentScope());
            pushStackTrace.run();
            
            var res = Optional.ofNullable((T) nativeFunctionHandler.invokeNativeMethod(symbolTable, currentStackTrace, functionType.getName(), params, functionType.getDefinedParamCount(), invokingUponExpressionType));
            
            currentStackTrace.popStackTraceElement();
            symbolTable.endFunctionCall();
            return res;
        }

        LOGGER.debug("params = {}", params);
        LOGGER.debug("functionParams = {}", functionParams);

        for (int i = 0; i < params.size(); i++) {
            var qilletniType = params.get(i);
            functionScope.define(SymbolImpl.createGenericSymbol(functionParams.get(i), qilletniType.getTypeClass(), qilletniType));
        }

        LOGGER.debug("! with current scope: {}", symbolTable);

        pushStackTrace.run();
        Optional<T> result = symbolTableMap.get(symbolTable).visitNode(functionType.getBody());

        currentStackTrace.popStackTraceElement();
        symbolTable.endFunctionCall();

        if (swapInvocationScope) {
            LOGGER.debug("UNSWAP scope!");
            symbolTable.unswapScope();
        }

        return result;
    }

    public <T extends QilletniType> Optional<T> invokeFunction(QilletniParser.Function_callContext ctx) {
        return invokeFunction(ctx, null);
    }

    public <T extends QilletniType> Optional<T> invokeFunction(QilletniParser.Function_callContext ctx, QilletniType invokedOn) {
        try {
            var id = ctx.ID().getText();

            var params = new ArrayList<QilletniType>();

            if (ctx.expr_list() != null) {
                params.addAll(symbolTableMap.get(symbolTable).visitNode(ctx.expr_list()));
            }

            return invokeFunction(id, params, invokedOn, () -> pushLocalStackTrace(ctx));
        } catch (QilletniException e) {
            if (e instanceof QilletniContextException qce) {
                if (!qce.isSourceSet()) {
                    qce.setSource(ctx);
                }

                if (qce.getQilletniStackTrace() == null) {
                    qce.setQilletniStackTrace(currentStackTrace);
                }

                throw e;
            } else {
                var qce = new QilletniContextException(ctx, e);

                qce.setQilletniStackTrace(currentStackTrace);

                throw qce;
            }
        }
    }

    /**
     * Pushed a stack trace from a Qilletni method.
     */
    private void pushLocalStackTrace(QilletniParser.Function_callContext ctx) {
        var parsedContext = StackUtils.parseContext(ctx);

        currentStackTrace.pushStackTraceElement(new QilletniStackTraceElementImpl("lib", parsedContext.fileName(), parsedContext.methodName(), parsedContext.line(), parsedContext.column()));
    }
    
    private QilletniStackTraceElement findCallingMethod() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        // Index 0 is getStackTrace, index 1 is findCallingMethod, index 2 is the caller
        if (stackTraceElements.length > 2) {
            var elem = stackTraceElements[2];
            return new QilletniStackTraceElementImpl("native", elem.getFileName(), elem.getMethodName(), elem.getLineNumber(), -1);
        }
        
        throw new RuntimeException("Couldn't identify calling method");
    }

    @Override
    public String toString() {
        return "FunctionInvokerImpl{" +
                "symbolTable=" + symbolTable +
                '}';
    }
}

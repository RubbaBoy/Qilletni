package dev.qilletni.impl.lang.internal;

import dev.qilletni.api.lang.internal.FunctionInvoker;
import dev.qilletni.api.lang.types.FunctionType;
import dev.qilletni.api.lang.types.QilletniType;

import java.util.List;
import java.util.Optional;

public class UnimplementedFunctionInvoker implements FunctionInvoker {
    
    @Override
    public <T extends QilletniType> Optional<T> invokeFunction(FunctionType alreadyFoundFunction, List<QilletniType> params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends QilletniType> Optional<T> invokeFunction(FunctionType alreadyFoundFunction, List<QilletniType> params, QilletniType invokedOn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends QilletniType> T invokeFunctionWithResult(FunctionType alreadyFoundFunction, List<QilletniType> params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends QilletniType> T invokeFunctionWithResult(FunctionType alreadyFoundFunction, List<QilletniType> params, QilletniType invokedOn) {
        throw new UnsupportedOperationException();
    }
}

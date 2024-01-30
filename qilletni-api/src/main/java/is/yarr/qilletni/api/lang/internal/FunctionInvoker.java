package is.yarr.qilletni.api.lang.internal;

import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.api.lang.types.FunctionType;
import is.yarr.qilletni.api.lang.types.QilletniType;

import java.util.List;
import java.util.Optional;

/**
 * An interface for invoking Qilletni methods from Java code.
 */
public interface FunctionInvoker {
    
    <T extends QilletniType> Optional<T> invokeFunction(FunctionType alreadyFoundFunction, List<QilletniType> params);
    
}

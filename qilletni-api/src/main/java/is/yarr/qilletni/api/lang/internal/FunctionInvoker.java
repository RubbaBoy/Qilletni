package is.yarr.qilletni.api.lang.internal;

import is.yarr.qilletni.api.lang.types.FunctionType;
import is.yarr.qilletni.api.lang.types.QilletniType;

import java.util.List;
import java.util.Optional;

/**
 * An interface for invoking Qilletni methods from Java code.
 */
public interface FunctionInvoker {

    /**
     * Invokes a function with the given parameters.
     *
     * @param alreadyFoundFunction The function to invoke
     * @param params               The parameters to pass to the function
     * @param <T>                  The type of the result
     * @return The result of the function, if any
     */
    <T extends QilletniType> Optional<T> invokeFunction(FunctionType alreadyFoundFunction, List<QilletniType> params);

    /**
     * Invokes a function with the given parameters.
     *
     * @param alreadyFoundFunction The function to invoke
     * @param params               The parameters to pass to the function
     * @param invokedOn            The type this was invoked on, if an extension method
     * @param <T>                  The type of the result
     * @return The result of the function, if any
     */
    <T extends QilletniType> Optional<T> invokeFunction(FunctionType alreadyFoundFunction, List<QilletniType> params, QilletniType invokedOn);

    /**
     * Invokes a function with the given parameters and returns the result, throwing if the function didn't return.
     *
     * @param alreadyFoundFunction The function to invoke
     * @param params               The parameters to pass to the function
     * @param <T>                  The type of the result
     * @return The result of the function
     */
    <T extends QilletniType> T invokeFunctionWithResult(FunctionType alreadyFoundFunction, List<QilletniType> params);

    /**
     * Invokes a function with the given parameters and returns the result, throwing if the function didn't return.
     *
     * @param alreadyFoundFunction The function to invoke
     * @param params               The parameters to pass to the function
     * @param invokedOn            The type this was invoked on, if an extension method
     * @param <T>                  The type of the result
     * @return The result of the function
     */
    <T extends QilletniType> T invokeFunctionWithResult(FunctionType alreadyFoundFunction, List<QilletniType> params, QilletniType invokedOn);

}

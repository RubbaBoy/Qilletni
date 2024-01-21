package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

import java.util.Optional;
import java.util.function.Supplier;

public non-sealed interface FunctionType extends QilletniType {
    String getName();

    /**
     * Gets the defined parameters of a function, including the first param if {@link #getOnType()} is not null.
     *
     * @return The parameters of the function definition
     */
    String[] getParams();

    /**
     * Gets the number of parameters the function is defined with. If this is native, this includes the self param.
     *
     * @return The number of parameters defined with
     */
    int getDefinedParamCount();

    /**
     * The number of parameters the function accepts when it is being invoked. This is the size of {@link #getParams()}.
     *
     * @return The number of parameters that are accepted upon invocation
     */
    int getInvokingParamCount();

    boolean isNative();

    QilletniTypeClass<?> getOnType();

    boolean isExternallyDefined();
    
    <T> T getBody();
}

package dev.qilletni.api.lang.types;

import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;

/**
 * A Qilletni type representing a function (that has yet to be invoked yet).
 */
public non-sealed interface FunctionType extends AnyType {

    /**
     * The name of the function.
     * 
     * @return The name of the function
     */
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

    /**
     * Checks if the function is defined natively.
     * 
     * @return If this is a native function
     */
    boolean isNative();

    /**
     * Checks if the function is static that does not need to be invoked on an instance of something.
     * 
     * @return If this is a static function
     */
    boolean isStatic();

    /**
     * If this is an extension function, this returns the type that it is on.
     * 
     * @return The type this function is on, or null if none
     */
    QilletniTypeClass<?> getOnType();

    /**
     * If the function was defined outside of an entity. Used with {@link #getOnType()}, this can tell if the function
     * has the first instance parameter or not.
     *
     * @return If the function is defined outside of an entity
     */
    boolean isExternallyDefined();

    /**
     * Gets the internal representation of the function's body that may be executed. This should only be used internally.
     * 
     * @return The internal representation of the function
     * @param <T> The type of the body
     */
    <T> T getBody();
}

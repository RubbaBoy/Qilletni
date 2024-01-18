package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.antlr.QilletniParser;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

import java.util.Arrays;
import java.util.Objects;

public final class FunctionType extends QilletniType {
    
    private final String name;
    private final String[] params;
    private final int invokingParamCount;
    private final int definedParamCount;
    private final boolean isNative;
    private final QilletniTypeClass<?> onType;
    private final boolean isExternallyDefined;
    
    // Only set if isNative is false
    private final QilletniParser.BodyContext bodyContext;

    private FunctionType(String name, String[] params, int invokingParamCount, int definedParamCount, boolean isNative, boolean isExternallyDefined, QilletniTypeClass<?> onType, QilletniParser.BodyContext bodyContext) {
        this.invokingParamCount = invokingParamCount;
        this.isNative = isNative;
        this.name = name;
        this.params = params;
        this.definedParamCount = definedParamCount;
        this.onType = onType;
        this.isExternallyDefined = isExternallyDefined;
        this.bodyContext = bodyContext;
    }
    
    public static FunctionType createImplementedFunction(String name, String[] params, int invokingParamCount, int definedParamCount, boolean isExternallyDefined, QilletniTypeClass<?> onType, QilletniParser.BodyContext bodyContext) {
        return new FunctionType(name, params, invokingParamCount, definedParamCount, false, isExternallyDefined, onType, bodyContext);
    }
    
    public static FunctionType createNativeFunction(String name, String[] params, int invokingParamCount, int definedParamCount, boolean isExternallyDefined, QilletniTypeClass<?> onType) {
        return new FunctionType(name, params, invokingParamCount, definedParamCount, true, isExternallyDefined, onType, null);
    }

    public String getName() {
        return name;
    }

    /**
     * Gets the defined parameters of a function, including the first param if {@link #getOnType()} is not null.
     * 
     * @return The parameters of the function definition
     */
    public String[] getParams() {
        return params;
    }

    /**
     * Gets the number of parameters the function is defined with. If this is native, this includes the self param.
     * 
     * @return The number of parameters defined with
     */
    public int getDefinedParamCount() {
        return definedParamCount;
    }

    /**
     * The number of parameters the function accepts when it is being invoked. This is the size of {@link #getParams()}.
     * 
     * @return The number of parameters that are accepted upon invocation
     */
    public int getInvokingParamCount() {
        return invokingParamCount;
    }

    public boolean isNative() {
        return isNative;
    }

    public QilletniTypeClass<?> getOnType() {
        return onType;
    }

    /**
     * If the function was defined outside of an entity. Used with {@link #getOnType()}, this can tell if the function
     * has the first instance parameter or not.
     * 
     * @return If the function is defined outside of an entity
     */
    public boolean isExternallyDefined() {
        return isExternallyDefined;
    }

    public QilletniParser.BodyContext getBodyContext() {
        return bodyContext;
    }

    @Override
    public String stringValue() {
        return String.format("%s%s(%s)",
                isNative ? "native " : "",
                name,
                String.join(",", params));
    }

    @Override
    public QilletniTypeClass<FunctionType> getTypeClass() {
        return QilletniTypeClass.FUNCTION;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        FunctionType that = (FunctionType) object;
        return Objects.equals(name, that.name) && Arrays.equals(params, that.params) && Objects.equals(onType, that.onType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, onType);
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }

    @Override
    public String toString() {
        return "FunctionType{" +
                "name='" + name + '\'' +
                ", params=" + Arrays.toString(params) +
                ", isNative=" + isNative +
                ", onType=" + onType +
                ", bodyContext=" + bodyContext +
                '}';
    }
}

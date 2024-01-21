package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.api.lang.types.FunctionType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Arrays;
import java.util.Objects;

public final class FunctionTypeImpl implements FunctionType {
    
    private final String name;
    private final String[] params;
    private final int invokingParamCount;
    private final int definedParamCount;
    private final boolean isNative;
    private final QilletniTypeClass<?> onType;
    private final boolean isExternallyDefined;
    
    // Only set if isNative is false
    private final ParserRuleContext body;

    private FunctionTypeImpl(String name, String[] params, int invokingParamCount, int definedParamCount, boolean isNative, boolean isExternallyDefined, QilletniTypeClass<?> onType, ParserRuleContext body) {
        this.invokingParamCount = invokingParamCount;
        this.isNative = isNative;
        this.name = name;
        this.params = params;
        this.definedParamCount = definedParamCount;
        this.onType = onType;
        this.isExternallyDefined = isExternallyDefined;
        this.body = body;
    }
    
    public static FunctionType createImplementedFunction(String name, String[] params, int invokingParamCount, int definedParamCount, boolean isExternallyDefined, QilletniTypeClass<?> onType, ParserRuleContext body) {
        return new FunctionTypeImpl(name, params, invokingParamCount, definedParamCount, false, isExternallyDefined, onType, body);
    }
    
    public static FunctionType createNativeFunction(String name, String[] params, int invokingParamCount, int definedParamCount, boolean isExternallyDefined, QilletniTypeClass<?> onType) {
        return new FunctionTypeImpl(name, params, invokingParamCount, definedParamCount, true, isExternallyDefined, onType, null);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getParams() {
        return params;
    }

    @Override
    public int getDefinedParamCount() {
        return definedParamCount;
    }

    @Override
    public int getInvokingParamCount() {
        return invokingParamCount;
    }

    @Override
    public boolean isNative() {
        return isNative;
    }

    @Override
    public QilletniTypeClass<?> getOnType() {
        return onType;
    }

    /**
     * If the function was defined outside of an entity. Used with {@link #getOnType()}, this can tell if the function
     * has the first instance parameter or not.
     * 
     * @return If the function is defined outside of an entity
     */
    @Override
    public boolean isExternallyDefined() {
        return isExternallyDefined;
    }

    @Override
    public <T> T getBody() {
        return (T) body;
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
        FunctionTypeImpl that = (FunctionTypeImpl) object;
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
                '}';
    }
}

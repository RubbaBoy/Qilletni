package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.antlr.QilletniParser;

import java.util.Arrays;
import java.util.Objects;

public final class FunctionType extends QilletniType {
    
    private final String name;
    private final String[] params;
    private final boolean isNative;
    private final Class<? extends QilletniType> onType;
    
    // Only set if isNative is false
    private final QilletniParser.BodyContext bodyContext;

    private FunctionType(String name, String[] params, boolean isNative, Class<? extends QilletniType> onType, QilletniParser.BodyContext bodyContext) {
        this.isNative = isNative;
        this.name = name;
        this.params = params;
        this.onType = onType;
        this.bodyContext = bodyContext;
    }
    
    public static FunctionType createImplementedFunction(String name, String[] params, Class<? extends QilletniType> onType, QilletniParser.BodyContext bodyContext) {
        return new FunctionType(name, params, false, onType, bodyContext);
    }
    
    public static FunctionType createNativeFunction(String name, String[] params, Class<? extends QilletniType> onType) {
        return new FunctionType(name, params, true, onType, null);
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

    public boolean isNative() {
        return isNative;
    }

    public Class<? extends QilletniType> getOnType() {
        return onType;
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
    public String typeName() {
        return "function";
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

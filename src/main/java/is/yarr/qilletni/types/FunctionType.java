package is.yarr.qilletni.types;

import is.yarr.qilletni.antlr.QilletniParser;

import java.util.Arrays;

public final class FunctionType implements QilletniType {
    
    private final String name;
    private final String[] params;
    private final boolean isNative;
    
    // Only set if isNative is false
    private final QilletniParser.BodyContext bodyContext;

    private FunctionType(String name, String[] params, boolean isNative, QilletniParser.BodyContext bodyContext) {
        this.isNative = isNative;
        this.name = name;
        this.params = params;
        this.bodyContext = bodyContext;
    }
    
    public static FunctionType createImplementedFunction(String name, String[] params, QilletniParser.BodyContext bodyContext) {
        return new FunctionType(name, params, false, bodyContext);
    }
    
    public static FunctionType createNativeFunction(String name, String[] params) {
        return new FunctionType(name, params, true, null);
    }

    public String getName() {
        return name;
    }

    public String[] getParams() {
        return params;
    }

    public boolean isNative() {
        return isNative;
    }

    public QilletniParser.BodyContext getBodyContext() {
        return bodyContext;
    }

    @Override
    public String toString() {
        return "FunctionType{" +
                "name='" + name + '\'' +
                ", params=" + Arrays.toString(params) +
                ", isNative=" + isNative +
                ", bodyContext=" + bodyContext +
                '}';
    }
}

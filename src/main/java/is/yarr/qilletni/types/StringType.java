package is.yarr.qilletni.types;

public final class StringType implements QilletniType {
    
    private String value;

    public StringType(String value) {
        this.value = value;
    }
    
    public static StringType fromType(QilletniType qilletniType) {
        return new StringType(String.valueOf(qilletniType.stringValue()));
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String stringValue() {
        return value;
    }

    @Override
    public String typeName() {
        return "string";
    }

    @Override
    public String toString() {
        return "StringType{" +
                "value='" + value + '\'' +
                '}';
    }
}

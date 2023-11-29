package is.yarr.qilletni.types;

public final class IntType implements QilletniType {
    
    private int value;

    public IntType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String stringValue() {
        return String.valueOf(value);
    }

    @Override
    public String typeName() {
        return "int";
    }

    @Override
    public String toString() {
        return "IntType{" +
                "value=" + value +
                '}';
    }
}

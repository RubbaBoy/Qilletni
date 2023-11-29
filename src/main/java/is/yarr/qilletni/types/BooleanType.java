package is.yarr.qilletni.types;

public final class BooleanType implements QilletniType {
    
    public static final BooleanType TRUE = new BooleanType(true);
    public static final BooleanType FALSE = new BooleanType(false);
    
    private boolean value;

    public BooleanType(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public String stringValue() {
        return String.valueOf(value);
    }

    @Override
    public String typeName() {
        return "boolean";
    }

    @Override
    public String toString() {
        return "BooleanType{" +
                "value=" + value +
                '}';
    }
}

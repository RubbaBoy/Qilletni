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
    public String toString() {
        return "IntType{" +
                "value=" + value +
                '}';
    }
}

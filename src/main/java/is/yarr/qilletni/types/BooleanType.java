package is.yarr.qilletni.types;

public final class BooleanType implements QilletniType {
    
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
}

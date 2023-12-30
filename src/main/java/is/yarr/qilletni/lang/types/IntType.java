package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

public final class IntType extends QilletniType {
    
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
    public QilletniTypeClass<IntType> getTypeClass() {
        return QilletniTypeClass.INT;
    }

    @Override
    public String toString() {
        return "IntType{" +
                "value=" + value +
                '}';
    }
}

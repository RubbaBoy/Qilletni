package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntType intType = (IntType) o;
        return value == intType.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "IntType{" +
                "value=" + value +
                '}';
    }
}

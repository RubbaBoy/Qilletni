package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.api.lang.types.IntType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

import java.util.Objects;

public final class IntTypeImpl implements IntType {
    
    private int value;

    public IntTypeImpl(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String stringValue() {
        return String.valueOf(value);
    }

    @Override
    public boolean qilletniEquals(QilletniType qilletniType) {
        if (!(qilletniType instanceof IntTypeImpl comparing)) {
            return false;
        }

        return this.value == comparing.value;
    }

    @Override
    public QilletniTypeClass<IntType> getTypeClass() {
        return QilletniTypeClass.INT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntTypeImpl intType = (IntTypeImpl) o;
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

package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.api.lang.types.DoubleType;
import is.yarr.qilletni.api.lang.types.IntType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

import java.util.Objects;

public final class IntTypeImpl implements IntType {
    
    private long value;

    public IntTypeImpl(long value) {
        this.value = value;
    }

    @Override
    public long getValue() {
        return value;
    }

    @Override
    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public String stringValue() {
        return String.valueOf(value);
    }

    @Override
    public boolean qilletniEquals(QilletniType qilletniType) {
        if (qilletniType instanceof IntType intType) {
            return value == intType.getValue();
        } else if (qilletniType instanceof DoubleType doubleType) {
            return value == doubleType.getValue();
        }
        
        return false;
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

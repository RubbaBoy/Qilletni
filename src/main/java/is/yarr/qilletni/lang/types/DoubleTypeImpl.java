package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.api.lang.types.DoubleType;
import is.yarr.qilletni.api.lang.types.IntType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

import java.util.Objects;

public class DoubleTypeImpl implements DoubleType {
    
    private double value;

    public DoubleTypeImpl(double value) {
        this.value = value;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean qilletniEquals(QilletniType qilletniType) {
        if (qilletniType instanceof DoubleType doubleType) {
            return value == doubleType.getValue();
        } else if (qilletniType instanceof IntType intType) {
            return value == intType.getValue();
        }
        
        return false;
    }

    @Override
    public String stringValue() {
        return String.valueOf(value);
    }

    @Override
    public QilletniTypeClass<?> getTypeClass() {
        return QilletniTypeClass.DOUBLE;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DoubleTypeImpl that = (DoubleTypeImpl) object;
        return Double.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "DoubleType{" +
                "value=" + value +
                '}';
    }
}

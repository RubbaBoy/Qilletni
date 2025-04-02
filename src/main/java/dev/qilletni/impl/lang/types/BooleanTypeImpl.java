package dev.qilletni.impl.lang.types;

import dev.qilletni.api.lang.types.BooleanType;
import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import dev.qilletni.impl.lang.exceptions.UnsupportedOperatorException;

public final class BooleanTypeImpl implements BooleanType {
    
    public static final BooleanType TRUE = new BooleanTypeImpl(true);
    public static final BooleanType FALSE = new BooleanTypeImpl(false);
    
    private boolean value;

    public BooleanTypeImpl(boolean value) {
        this.value = value;
    }

    @Override
    public boolean getValue() {
        return value;
    }

    @Override
    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public String stringValue() {
        return String.valueOf(value);
    }

    @Override
    public boolean qilletniEquals(QilletniType qilletniType) {
        if (!(qilletniType instanceof BooleanTypeImpl comparing)) {
            return false;
        }
        
        return this.value == comparing.value;
    }

    @Override
    public QilletniType plusOperator(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "+");
    }

    @Override
    public void plusOperatorInPlace(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "+");
    }

    @Override
    public QilletniType minusOperator(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "-");
    }

    @Override
    public void minusOperatorInPlace(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "-");
    }

    @Override
    public QilletniTypeClass<BooleanType> getTypeClass() {
        return QilletniTypeClass.BOOLEAN;
    }

    @Override
    public String toString() {
        return "BooleanType{" +
                "value=" + value +
                '}';
    }
}

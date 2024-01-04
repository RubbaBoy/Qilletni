package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

public final class BooleanType extends QilletniType {
    
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
    public boolean qilletniEquals(QilletniType qilletniType) {
        if (!(qilletniType instanceof BooleanType comparing)) {
            return false;
        }
        
        return this.value == comparing.value;
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

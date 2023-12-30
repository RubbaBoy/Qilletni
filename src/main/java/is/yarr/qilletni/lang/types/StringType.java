package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

public final class StringType extends QilletniType {
    
    private String value;

    public StringType(String value) {
        this.value = value;
    }
    
    public static StringType fromType(QilletniType qilletniType) {
        return new StringType(String.valueOf(qilletniType.stringValue()));
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String stringValue() {
        return value;
    }

    @Override
    public QilletniTypeClass<StringType> getTypeClass() {
        return QilletniTypeClass.STRING;
    }

    @Override
    public String toString() {
        return "StringType{" +
                "value='" + value + '\'' +
                '}';
    }
}

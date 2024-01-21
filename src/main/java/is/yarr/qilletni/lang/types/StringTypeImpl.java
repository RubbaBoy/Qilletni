package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.StringType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

import java.util.Objects;

public final class StringTypeImpl implements StringType {
    
    private String value;

    public StringTypeImpl(String value) {
        this.value = value;
    }
    
    public static StringType fromType(QilletniType qilletniType) {
        return new StringTypeImpl(String.valueOf(qilletniType.stringValue()));
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String stringValue() {
        return value;
    }

    @Override
    public boolean qilletniEquals(QilletniType qilletniType) {
        if (!(qilletniType instanceof StringTypeImpl comparing)) {
            return false;
        }

        return this.value.equals(comparing.value);
    }

    @Override
    public QilletniTypeClass<StringType> getTypeClass() {
        return QilletniTypeClass.STRING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringTypeImpl that = (StringTypeImpl) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "StringType{" +
                "value='" + value + '\'' +
                '}';
    }
}

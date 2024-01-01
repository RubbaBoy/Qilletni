package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.exceptions.TypeMismatchException;
import is.yarr.qilletni.lang.exceptions.UnsetJavaReferenceException;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

import java.util.Objects;
import java.util.Optional;

public final class JavaType extends QilletniType {
    
    private Object reference;

    public JavaType(Object reference) {
        this.reference = reference;
    }

    public <T> Optional<T> getOptionalReference(Class<T> refType) {
        if (reference == null) {
            return Optional.empty();
        }

        if (!refType.isInstance(reference)) {
            throw new TypeMismatchException(String.format("Expected java reference of %s but was actually %s", refType.getCanonicalName(), reference.getClass().getCanonicalName()));
        }

        return Optional.of(refType.cast(reference));
    }

    public <T> T getReference(Class<T> refType) {
        return getOptionalReference(refType).orElseThrow(UnsetJavaReferenceException::new);
    }

    public void setReference(Object reference) {
        this.reference = reference;
    }

    @Override
    public String stringValue() {
        return String.valueOf(reference);
    }

    @Override
    public QilletniTypeClass<JavaType> getTypeClass() {
        return QilletniTypeClass.JAVA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavaType javaType = (JavaType) o;
        return Objects.equals(reference, javaType.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }

    @Override
    public String toString() {
        return "JavaType{" +
                "reference=" + reference +
                '}';
    }
}

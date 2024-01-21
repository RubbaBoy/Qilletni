package is.yarr.qilletni.api.lang.types;

import java.util.Optional;

public non-sealed interface JavaType extends QilletniType {
    <T> Optional<T> getOptionalReference(Class<T> refType);

    <T> T getReference(Class<T> refType);

    void setReference(Object reference);
}

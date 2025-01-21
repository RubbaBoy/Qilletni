package is.yarr.qilletni.api.lang.types;

import java.util.Optional;

/**
 * A Qilletni type that references any Java type instance. This is so native methods can access references that
 * Qilletni can deal with.
 */
public non-sealed interface JavaType extends AnyType {

    /**
     * Gets the reference this type holds as an optional, giving an empty optional if not set yet or <code>empty</code>.
     * 
     * @param refType The class type the reference is assumed to be
     * @return The optional of the type's reference
     * @param <T> The type of the reference
     */
    <T> Optional<T> getOptionalReference(Class<T> refType);

    /**
     * Gets the reference held by the type, or null.
     * 
     * @param refType class type the reference is assumed to be
     * @return The type's reference, or null
     * @param <T> The type of the reference
     */
    <T> T getReference(Class<T> refType);

    /**
     * Sets the type's reference to anything, overriding any previous reference.
     * 
     * @param reference The reference the type should hold
     */
    void setReference(Object reference);
}

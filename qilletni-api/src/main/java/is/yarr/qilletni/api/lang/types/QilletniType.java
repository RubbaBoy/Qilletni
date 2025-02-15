package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

/**
 * Internal types for Qilletni programs.
 */
public sealed interface QilletniType permits AnyType, ImportAliasType, StaticEntityType {

    /**
     * Provides the string representation of the Qilletni type, used when the type is printed via Qilletni.
     *
     * @return A string representing the type
     */
    String stringValue();

    /**
     * Compares the current QilletniType with another QilletniType to determine equality.
     *
     * @param qilletniType The QilletniType to compare against the current instance.
     * @return A boolean indicating whether the two QilletniType instances are considered equal.
     */
    default boolean qilletniEquals(QilletniType qilletniType) {
        return false;
    }

    /**
     * Performs an addition operation on the current instance of a QilletniType and the specified QilletniType,
     * returning the new value.
     *
     * @param qilletniType The other QilletniType used in the addition operation.
     * @return A new QilletniType representing the result of the addition operation.
     */
    QilletniType plusOperator(QilletniType qilletniType);

    /**
     * Performs an addition operation on the current instance of a QilletniType and the specified QilletniType. This
     * QilletniType instance is mutated to reflect the result, unlike {@link #plusOperator(QilletniType)}.
     *
     * @param qilletniType The other QilletniType used in the addition operation.
     */
    void plusOperatorInPlace(QilletniType qilletniType);

    /**
     * Performs a subtraction operation between the current instance of a QilletniType and the provided QilletniType,
     * returning the new value.
     *
     * @param qilletniType The other QilletniType used in the subtraction operation.
     * @return A new QilletniType representing the result of the subtraction operation.
     */
    QilletniType minusOperator(QilletniType qilletniType);

    /**
     * Performs a subtraction operation between the current instance of a QilletniType and the provided QilletniType. This
     * QilletniType instance is mutated to reflect the result, unlike {@link #minusOperator(QilletniType)}.
     *
     * @param qilletniType The other QilletniType used in the subtraction operation.
     */
    void minusOperatorInPlace(QilletniType qilletniType);

    /**
     * Retrieves the type name of the current QilletniType instance. If an Entity, it will return the name of the
     * Entity.
     *
     * @return The type name as a string, representing the type of the QilletniType instance
     */
    default String typeName() {
        return getTypeClass().getTypeName();
    }

    /**
     * Retrieves the type class of the current QilletniType instance. The type class provides additional information
     * about the internal classification of the instance.
     *
     * @return The corresponding {@link QilletniTypeClass} of the current QilletniType instance
     */
    QilletniTypeClass<?> getTypeClass();
}

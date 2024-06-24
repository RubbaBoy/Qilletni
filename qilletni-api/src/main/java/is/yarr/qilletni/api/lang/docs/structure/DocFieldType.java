package is.yarr.qilletni.api.lang.docs.structure;

/**
 * Documentation recording the type of a field, return value, etc.
 */
public record DocFieldType(FieldType fieldType, String identifier) {
    
    public enum FieldType {
        QILLETNI,
        JAVA
    }
    
}

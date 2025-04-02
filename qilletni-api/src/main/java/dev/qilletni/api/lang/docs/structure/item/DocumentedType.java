package dev.qilletni.api.lang.docs.structure.item;

public sealed interface DocumentedType permits DocumentedTypeEntity, DocumentedTypeEntityConstructor, DocumentedTypeField, DocumentedTypeFunction {

    /**
     * The library this type is defined in, and may be used to import from.
     * 
     * @return The name of the owning library
     */
    String libraryName();
    
    /**
     * The file path this type is defined in, and may be used to import from.
     * 
     * @return The relative path of the file
     */
    String importPath();
}

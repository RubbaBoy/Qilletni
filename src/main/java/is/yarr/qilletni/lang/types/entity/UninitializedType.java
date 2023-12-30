package is.yarr.qilletni.lang.types.entity;

import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

public class UninitializedType {
    
    private final QilletniTypeClass<?> nativeTypeClass;
    private final EntityDefinition entityDefinition;

    public UninitializedType(QilletniTypeClass<?> nativeTypeClass) {
        this.nativeTypeClass = nativeTypeClass;
        this.entityDefinition = null;
    }

    public UninitializedType(EntityDefinition entityDefinition) {
        this.entityDefinition = entityDefinition;
        this.nativeTypeClass = null;
    }
    
    public boolean isNative() {
        return nativeTypeClass != null;
    }

    public QilletniTypeClass<?> getNativeTypeClass() {
        return nativeTypeClass;
    }

    public EntityDefinition getEntityDefinition() {
        return entityDefinition;
    }
    
    public String getTypeName() {
        if (isNative()) {
            return nativeTypeClass.getTypeName();
        } else {
            return entityDefinition.getTypeName();
        }
    }

    @Override
    public String toString() {
        var typeString = "nativeTypeClass=" + nativeTypeClass;
        
        if (!isNative()) {
            typeString = "entityDefinition=" + entityDefinition; 
        }
        
        return "UninitializedType{" + typeString + '}';
    }
}

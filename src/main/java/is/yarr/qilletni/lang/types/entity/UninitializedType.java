package is.yarr.qilletni.lang.types.entity;

import is.yarr.qilletni.lang.types.QilletniType;

public class UninitializedType {
    
    private final Class<? extends QilletniType> nativeTypeClass;
    private final EntityDefinition entityDefinition;

    public UninitializedType(Class<? extends QilletniType> nativeTypeClass) {
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

    public Class<? extends QilletniType> getNativeTypeClass() {
        return nativeTypeClass;
    }

    public EntityDefinition getEntityDefinition() {
        return entityDefinition;
    }
    
    public String getTypeName() {
        if (isNative()) {
            return nativeTypeClass.getSimpleName();
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

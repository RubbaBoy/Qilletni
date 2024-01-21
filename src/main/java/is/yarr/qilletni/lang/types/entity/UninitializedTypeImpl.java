package is.yarr.qilletni.lang.types.entity;

import is.yarr.qilletni.api.lang.types.entity.EntityDefinition;
import is.yarr.qilletni.api.lang.types.entity.UninitializedType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

public class UninitializedTypeImpl implements UninitializedType {
    
    private final QilletniTypeClass<?> nativeTypeClass;
    private final EntityDefinition entityDefinition;

    public UninitializedTypeImpl(QilletniTypeClass<?> nativeTypeClass) {
        this.nativeTypeClass = nativeTypeClass;
        this.entityDefinition = null;
    }

    public UninitializedTypeImpl(EntityDefinition entityDefinition) {
        this.entityDefinition = entityDefinition;
        this.nativeTypeClass = null;
    }
    
    @Override
    public boolean isEntity() {
        return nativeTypeClass == null;
    }

    @Override
    public QilletniTypeClass<?> getNativeTypeClass() {
        return nativeTypeClass;
    }

    @Override
    public EntityDefinition getEntityDefinition() {
        return entityDefinition;
    }
    
    @Override
    public String getTypeName() {
        if (!isEntity()) {
            return nativeTypeClass.getTypeName();
        } else {
            return entityDefinition.getTypeName();
        }
    }

    @Override
    public String toString() {
        var typeString = "nativeTypeClass=" + nativeTypeClass;
        
        if (isEntity()) {
            typeString = "entityDefinition=" + entityDefinition; 
        }
        
        return "UninitializedType{" + typeString + '}';
    }
}

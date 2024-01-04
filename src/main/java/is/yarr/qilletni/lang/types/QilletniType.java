package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal types for Qilletni programs.
 */
public sealed abstract class QilletniType permits BooleanType, CollectionType, EntityType, FunctionType, IntType, JavaType, ListType, SongType, StringType, WeightsType {
    public abstract String stringValue();
    
    public boolean qilletniEquals(QilletniType qilletniType) {
        return false;
    }

    public String typeName() {
        return getTypeClass().getTypeName();
    }
    
    public abstract QilletniTypeClass<?> getTypeClass();
}

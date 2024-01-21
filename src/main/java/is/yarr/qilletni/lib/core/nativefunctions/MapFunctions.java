package is.yarr.qilletni.lib.core.nativefunctions;

import is.yarr.qilletni.lang.internal.NativeOn;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.JavaType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.lang.types.JavaTypeImpl;

import java.util.HashMap;

public class MapFunctions {
    
    public static JavaType _emptyJavaMap() {
        return new JavaTypeImpl(new HashMap<>());
    }

    @NativeOn("Map")
    public static void put(EntityType entity, QilletniType key, QilletniType value) {
        JavaType javaType = entity.getEntityScope().<JavaTypeImpl>lookup("_map").getValue();
        HashMap<QilletniType, QilletniType> hashMap = javaType.getReference(HashMap.class);
        
        hashMap.put(key, value);
    }

    @NativeOn("Map")
    public static QilletniType get(EntityType entity, QilletniType key) {
        JavaType javaType = entity.getEntityScope().<JavaTypeImpl>lookup("_map").getValue();
        HashMap<QilletniType, QilletniType> hashMap = javaType.getReference(HashMap.class);
        
        return hashMap.get(key);
    }
    
    @NativeOn("Map")
    public static boolean containsKey(EntityType entity, QilletniType key) {
        JavaType javaType = entity.getEntityScope().<JavaTypeImpl>lookup("_map").getValue();
        HashMap<QilletniType, QilletniType> hashMap = javaType.getReference(HashMap.class);
        
        return hashMap.containsKey(key);
    }
    
    @NativeOn("Map")
    public static boolean containsValue(EntityType entity, QilletniType key) {
        JavaType javaType = entity.getEntityScope().<JavaTypeImpl>lookup("_map").getValue();
        HashMap<QilletniType, QilletniType> hashMap = javaType.getReference(HashMap.class);
        
        return hashMap.containsValue(key);
    }
}

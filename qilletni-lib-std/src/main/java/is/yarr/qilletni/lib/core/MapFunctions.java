package is.yarr.qilletni.lib.core;

import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.JavaType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lib.NativeOn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapFunctions {
    
    public static Object _emptyJavaMap() {
        return new HashMap<>();
    }

    @NativeOn("Map")
    public static void put(EntityType entity, QilletniType key, QilletniType value) {
        JavaType javaType = entity.getEntityScope().<JavaType>lookup("_map").getValue();
        HashMap<QilletniType, QilletniType> hashMap = javaType.getReference(HashMap.class);
        
        hashMap.put(key, value);
    }

    @NativeOn("Map")
    public static QilletniType get(EntityType entity, QilletniType key) {
        JavaType javaType = entity.getEntityScope().<JavaType>lookup("_map").getValue();
        HashMap<QilletniType, QilletniType> hashMap = javaType.getReference(HashMap.class);
        
        return hashMap.get(key);
    }
    
    @NativeOn("Map")
    public static boolean containsKey(EntityType entity, QilletniType key) {
        JavaType javaType = entity.getEntityScope().<JavaType>lookup("_map").getValue();
        HashMap<QilletniType, QilletniType> hashMap = javaType.getReference(HashMap.class);
        
        return hashMap.containsKey(key);
    }
    
    @NativeOn("Map")
    public static boolean containsValue(EntityType entity, QilletniType key) {
        JavaType javaType = entity.getEntityScope().<JavaType>lookup("_map").getValue();
        HashMap<QilletniType, QilletniType> hashMap = javaType.getReference(HashMap.class);
        
        return hashMap.containsValue(key);
    }
    
    @NativeOn("Map")
    public static List<QilletniType> keys(EntityType entity) {
        JavaType javaType = entity.getEntityScope().<JavaType>lookup("_map").getValue();
        HashMap<QilletniType, QilletniType> hashMap = javaType.getReference(HashMap.class);
        
        return new ArrayList<>(hashMap.keySet());
    }
    
    @NativeOn("Map")
    public static List<QilletniType> values(EntityType entity) {
        JavaType javaType = entity.getEntityScope().<JavaType>lookup("_map").getValue();
        HashMap<QilletniType, QilletniType> hashMap = javaType.getReference(HashMap.class);
        
        return new ArrayList<>(hashMap.values());
    }
}

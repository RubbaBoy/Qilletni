package is.yarr.qilletni.lib.core.nativefunctions;

import is.yarr.qilletni.lang.internal.NativeOn;
import is.yarr.qilletni.lang.types.EntityType;
import is.yarr.qilletni.lang.types.QilletniType;

import java.util.HashMap;

@NativeOn("Map")
public class MapFunctions {
    
//    private void p() {
//        var t = new HashMap<>();
//        t.pu
//    }
    
    public static void put(EntityType entity, QilletniType key, QilletniType value) {
        System.out.println("entity = " + entity + ", key = " + key + ", value = " + value);
        
//        entity.getEntityScope().lookup("")
    }
    
}

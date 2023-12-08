package is.yarr.qilletni.lang.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal types for Qilletni programs.
 */
public sealed abstract class QilletniType permits BooleanType, CollectionType, EntityType, FunctionType, IntType, SongType, StringType, WeightsType {
    
    final List<FunctionType> associatedFunctions = new ArrayList<>();
//    final List<String> associatedProperties = new ArrayList<>();
    
    /**
     * Functions that may be invoked upon this type.
     * 
     * @return Functions that may be invoked on this type
     */
    public List<FunctionType> getAssociatedFunctions() {
        return associatedFunctions;
    }

// TODO: I'm not sure if I really want/need proeprties

//    /**
//     * Properties that may be accessed via this type.
//     * 
//     * @return The properties that can be accessed
//     */
//    public List<String> getAssociatedProperties() {
//        return associatedProperties;
//    }
//
//    /**
//     * Resolves a property from {@link #getAssociatedProperties()}.
//     * 
//     * @param property The property being queried
//     * @return The property's value
//     */
//    abstract QilletniType resolveProperty(String property);
    
    public abstract String stringValue();

    public abstract String typeName();
}

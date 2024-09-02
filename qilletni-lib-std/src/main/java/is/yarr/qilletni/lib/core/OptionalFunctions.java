package is.yarr.qilletni.lib.core;

import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.JavaType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.StaticEntityType;
import is.yarr.qilletni.api.lib.NativeOn;
import is.yarr.qilletni.lib.core.exceptions.OptionalEmptyValueException;

import java.util.Optional;

public class OptionalFunctions {
    
    @NativeOn("Optional")
    public static QilletniType getValue(EntityType entityType) {
        var optionalValue = entityType.getEntityScope().<JavaType>lookup("_value").getValue();
        Optional<QilletniType> qilletniTypeOptional = optionalValue.getReference(Optional.class);
        
        return qilletniTypeOptional.orElseThrow(() -> new OptionalEmptyValueException("Optional value is empty"));
    }
}

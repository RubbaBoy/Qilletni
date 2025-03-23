package is.yarr.qilletni.lang.types.conversion;

import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.lang.exceptions.NoTypeAdapterException;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapterRegistrar;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility to provide bulk conversions. This is broken off as to not create a cyclical dependency between
 * {@link TypeConverterImpl} and {@link is.yarr.qilletni.lang.types.entity.EntityInitializerImpl}.
 * 
 * TODO: How gross is this?
 */
public class BulkTypeConversion {
    
    private final TypeAdapterRegistrar typeAdapterRegistrar;

    public BulkTypeConversion(TypeAdapterRegistrar typeAdapterRegistrar) {
        this.typeAdapterRegistrar = typeAdapterRegistrar;
    }

    public List<QilletniType> convertToQilletniTypes(List<Object> list) {
        var adaptedArgs = new ArrayList<QilletniType>();

        for (var arg : list) {
            if (arg instanceof QilletniType qilletniType) {
                adaptedArgs.add(qilletniType);
                continue;
            }

            var typeAdapter = typeAdapterRegistrar.findAnyTypeAdapter(arg.getClass())
                    .orElseThrow(() -> new NoTypeAdapterException(arg.getClass()));

            adaptedArgs.add(typeAdapter.convertCastedType(arg));
        }

        return adaptedArgs;
    }
    
}

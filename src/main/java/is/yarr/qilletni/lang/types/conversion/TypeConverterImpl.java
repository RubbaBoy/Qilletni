package is.yarr.qilletni.lang.types.conversion;

import is.yarr.qilletni.api.lang.table.Symbol;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.conversion.TypeConverter;
import is.yarr.qilletni.api.lang.types.entity.EntityInitializer;
import is.yarr.qilletni.lang.exceptions.NoTypeAdapterException;
import is.yarr.qilletni.lang.exceptions.java.RecordConversionException;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapter;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapterRegistrar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeConverterImpl implements TypeConverter {
    
    private final TypeAdapterRegistrar typeAdapterRegistrar;
    private final EntityInitializer entityInitializer;

    public TypeConverterImpl(TypeAdapterRegistrar typeAdapterRegistrar, EntityInitializer entityInitializer) {
        this.typeAdapterRegistrar = typeAdapterRegistrar;
        this.entityInitializer = entityInitializer;
    }

    @Override
    public <T> T convertToJavaType(QilletniType qilletniType, Class<T> clazz) {
        TypeAdapter<T, ? extends QilletniType> typeAdapter = typeAdapterRegistrar.findTypeAdapter(clazz, qilletniType.getClass())
                .orElseThrow(() -> new NoTypeAdapterException(clazz, qilletniType.getClass()));
        
        return typeAdapter.convertCastedType(qilletniType);
    }

    @Override
    public QilletniType convertToQilletniType(Object object) {
        if (object instanceof QilletniType qilletniType) {
            return qilletniType;
        }
        
        TypeAdapter<QilletniType, ?> qilletniTypeTypeAdapter = typeAdapterRegistrar.findAnyTypeAdapter(object.getClass())
                .orElseThrow(() -> new NoTypeAdapterException(object.getClass()));

        return qilletniTypeTypeAdapter.convertCastedType(object);
    }

    @Override
    public EntityType convertFromRecordToEntity(String entityName, Object object) {
        if (!object.getClass().isRecord()) {
            throw new RecordConversionException("Expected a record for conversion to %s".formatted(entityName));
        }

        // Order matters here! TODO: Make it only by name
        
        Object[] values = RecordUtility.extractRecordValues(object);
        
        return entityInitializer.initializeEntity(entityName, values);
    }

    @Override
    public <T> T convertFromEntityToRecord(EntityType entity, Class<T> clazz) {
        try {
            var constructor = RecordUtility.getRecordConstructor(clazz);
            var params = constructor.getParameters();

            var symbolMap = entity.getEntityScope().getAllSymbols();
            
            var transformedParams = new Object[constructor.getParameterCount()];

            for (int i = 0; i < params.length; i++) {
                var param = params[i];
                var symbol = symbolMap.get(param.getName()); // Match up the parameter name with the entity property. Unused entity properties will be ignored
                
                if (symbol == null) {
                    throw new RecordConversionException("Missing entity property for parameter: %s".formatted(param.getName()));
                }
                
                transformedParams[i] = convertToJavaType(symbol.getValue(), param.getType());
            }
            
            return RecordUtility.createRecord(clazz, transformedParams);
        } catch (ReflectiveOperationException e) {
            throw new RecordConversionException("Failed to create record instance for: %s".formatted(clazz.getName()));
        }
    }

}

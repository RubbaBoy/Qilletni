package is.yarr.qilletni.lang.types.conversion;

import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.conversion.TypeConverter;
import is.yarr.qilletni.api.lang.types.entity.EntityInitializer;
import is.yarr.qilletni.lang.exceptions.NoTypeAdapterException;
import is.yarr.qilletni.lang.exceptions.java.RecordConversionException;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapter;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapterRegistrar;

import java.util.List;

public class TypeConverterImpl implements TypeConverter {
    
    private final TypeAdapterRegistrar typeAdapterRegistrar;
    private final EntityInitializer entityInitializer;
    private final BulkTypeConversion bulkTypeConversion;

    public TypeConverterImpl(TypeAdapterRegistrar typeAdapterRegistrar, EntityInitializer entityInitializer, BulkTypeConversion bulkTypeConversion) {
        this.typeAdapterRegistrar = typeAdapterRegistrar;
        this.entityInitializer = entityInitializer;
        this.bulkTypeConversion = bulkTypeConversion;
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
    public List<QilletniType> convertToQilletniTypes(List<Object> list) {
        return bulkTypeConversion.convertToQilletniTypes(list);
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
                // Match up the parameter name with the entity property. Unused entity properties will be ignored
                // Note this won't work well if there are param names such as `x` and `_x` in the entity
                
                var symbol = symbolMap.get(param.getName());
                var privateSymbol = symbolMap.get("_%s".formatted(param.getName())); // check for private property of the same name
                
                if (symbol == null && privateSymbol == null) {
                    throw new RecordConversionException("Missing entity property for parameter: %s".formatted(param.getName()));
                }
                
                if (symbol == null) {
                    symbol = privateSymbol;
                }
                
                transformedParams[i] = convertToJavaType(symbol.getValue(), param.getType());
            }
            
            return RecordUtility.createRecord(clazz, transformedParams);
        } catch (ReflectiveOperationException e) {
            throw new RecordConversionException("Failed to create record instance for: %s".formatted(clazz.getName()));
        }
    }

}

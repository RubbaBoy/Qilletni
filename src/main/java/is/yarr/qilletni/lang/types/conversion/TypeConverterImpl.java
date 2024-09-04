package is.yarr.qilletni.lang.types.conversion;

import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.conversion.TypeConverter;
import is.yarr.qilletni.lang.exceptions.NoTypeAdapterException;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapter;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapterRegistrar;

public class TypeConverterImpl implements TypeConverter {
    
    private final TypeAdapterRegistrar typeAdapterRegistrar;

    public TypeConverterImpl(TypeAdapterRegistrar typeAdapterRegistrar) {
        this.typeAdapterRegistrar = typeAdapterRegistrar;
    }

    @Override
    public <T> T convertToJavaType(QilletniType qilletniType, Class<T> clazz) {
        TypeAdapter<T, ? extends QilletniType> typeAdapter = typeAdapterRegistrar.findTypeAdapter(clazz, qilletniType.getClass())
                .orElseThrow(() -> new NoTypeAdapterException(clazz, qilletniType.getClass()));
        
        return typeAdapter.convertCastedType(qilletniType);
    }

    @Override
    public QilletniType convertToQilletniType(Object object) {
        TypeAdapter<QilletniType, ?> qilletniTypeTypeAdapter = typeAdapterRegistrar.findAnyTypeAdapter(object.getClass())
                .orElseThrow(() -> new NoTypeAdapterException(object.getClass()));

        return qilletniTypeTypeAdapter.convertCastedType(object);
    }
}

package is.yarr.qilletni.lang.internal.adapter;

import is.yarr.qilletni.api.lang.types.QilletniType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeAdapterRegistrar {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TypeAdapterRegistrar.class);
    
    private final List<RegisteredTypeAdapter<?, ?>> typeAdapters = new ArrayList<>();
    
    public <R, T> void registerTypeAdapter(Class<R> returnType, Class<T> convertingType, TypeAdapter<R, T> typeAdapter) {
        typeAdapters.add(new RegisteredTypeAdapter<>(returnType, convertingType, typeAdapter));
    }
    
    public <R, T> Optional<TypeAdapter<R, T>> findTypeAdapter(Class<R> returnType, Class<T> convertingType) {
        return typeAdapters.stream()
                .filter(adapter -> adapter.returnType.equals(returnType) && adapter.convertingType.equals(convertingType))
                .map(adapter -> (TypeAdapter<R, T>) adapter.typeAdapter)
                .findFirst();
    }

    /**
     * Finds a type adapter for the return value of a native method. If multiple are found, a warning is logged and the
     * first one is chosen.
     * 
     * @param convertingType The type to convert to a Qilletni type
     * @return The found type adapter, if any
     * @param <T> The type being adapted
     */
    public <T> Optional<TypeAdapter<QilletniType, T>> findReturningTypeAdapter(Class<T> convertingType) {
        var foundAdapters = typeAdapters.stream()
                .filter(adapter -> adapter.convertingType.equals(convertingType))
                .map(adapter -> (TypeAdapter<QilletniType, T>) adapter.typeAdapter)
                .toList();
        
        if (foundAdapters.isEmpty()) {
            return Optional.empty();
        }
        
        if (foundAdapters.size() > 1) {
            LOGGER.warn("Multiple returning adapters found for {}! Choosing the first adapter.", convertingType.getCanonicalName());
        }
        
        return Optional.of(foundAdapters.get(0));
    }

    record RegisteredTypeAdapter<R, T>(Class<R> returnType, Class<T> convertingType, TypeAdapter<R, T> typeAdapter) {
    }
    
}

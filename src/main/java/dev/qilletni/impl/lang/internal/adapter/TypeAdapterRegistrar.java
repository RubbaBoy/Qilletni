package dev.qilletni.impl.lang.internal.adapter;

import dev.qilletni.api.lang.types.QilletniType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class TypeAdapterRegistrar {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TypeAdapterRegistrar.class);
    
    private final List<RegisteredTypeAdapter<?, ?>> typeAdapters = new ArrayList<>();
    
    public <R, T> void registerExactTypeAdapter(Class<R> returnType, Class<T> convertingType, TypeAdapter<R, T> typeAdapter) {
        typeAdapters.add(new RegisteredTypeAdapter<>(returnType, convertingType, typeAdapter, true));
    }
    
    public <R, T> void registerTypeAdapter(Class<R> returnType, Class<T> convertingType, TypeAdapter<R, T> typeAdapter) {
        typeAdapters.add(new RegisteredTypeAdapter<>(returnType, convertingType, typeAdapter, false));
    }
    
    public <R, T> Optional<TypeAdapter<R, T>> findTypeAdapter(Class<R> returnType, Class<T> convertingType) {
        return typeAdapters.stream()
                .filter(adapter -> adapter.returnType.equals(returnType) && adapter.convertingType.equals(convertingType))
                .map(adapter -> (TypeAdapter<R, T>) adapter.typeAdapter)
                .findFirst();
    }

    /**
     * Finds a type adapter that converts any java type to any {@link QilletniType}. First, exact matches are checked,
     * and then non-exact class matches are checked.
     * 
     * @param convertingType The type to convert to a Qilletni type
     * @return The found type adapter, if any
     * @param <T> The type being adapted
     */
    public <T> Optional<TypeAdapter<QilletniType, T>> findAnyTypeAdapter(Class<T> convertingType) {
        return typeAdapters.stream().filter(RegisteredTypeAdapter::exactClassMatch)
                .filter(adapter -> adapter.convertingType.equals(convertingType))
                .findFirst()
                .or(() -> typeAdapters.stream().filter(Predicate.not(RegisteredTypeAdapter::exactClassMatch))
                            .filter(adapter -> adapter.convertingType.isAssignableFrom(convertingType))
                            .findFirst())
                .map(adapter -> (TypeAdapter<QilletniType, T>) adapter.typeAdapter);
    }

    record RegisteredTypeAdapter<R, T>(Class<R> returnType, Class<T> convertingType, TypeAdapter<R, T> typeAdapter, boolean exactClassMatch) {
    }
    
}

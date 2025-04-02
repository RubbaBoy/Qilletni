package dev.qilletni.impl.lang.types.list;

import dev.qilletni.impl.lang.exceptions.ListTransformerNotFoundException;
import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import dev.qilletni.impl.lang.types.SongTypeImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A class used to convert a {@link QilletniType} from one to another. For example, a type adapter may convert a string
 * of an ID or URL to a {@link SongTypeImpl}. They are typically used in lists, to ensure uniform types by converting
 * from the given type into the required type.
 */
public class ListTypeTransformer {
    
    private final List<Transformer<?, ?>> transformers = new ArrayList<>();

    /**
     * Registers a transformer.
     * 
     * @param from                The type this converts from
     * @param to                  The type the {@link QilletniType} will end up as
     * @param transformerFunction The function to actually transform between types
     * @param <T>                 The to type
     * @param <F>                 The from type
     */
    public <T extends QilletniType, F extends QilletniType> void registerListTransformer(QilletniTypeClass<F> from, QilletniTypeClass<T> to, Function<F, T> transformerFunction) {
        transformers.add(new Transformer<>(from, to, transformerFunction));
    }

    /**
     * Checks if a transformer exists from one type to another.
     * 
     * @param from The starting type
     * @param to   The ending type
     * @return If the transformer exists
     */
    public boolean doesTransformerExist(QilletniTypeClass<?> to, QilletniTypeClass<?> from) {
        return transformers.stream().anyMatch(transformer -> transformer.matchesTypes(from, to));
    }

    /**
     * Transforms a type from the given one to a specified type. If no transformer is found, an exception is thrown.
     * 
     * @param to           The resulting type that should be transformed to
     * @param qilletniType The type to convert to
     * @return The transformed type
     * @param <T> The type to transform to
     */
    public <T extends QilletniType> QilletniType transformType(QilletniTypeClass<T> to, QilletniType qilletniType) {
        return transformers.stream().filter(transformer -> transformer.matchesTypes(qilletniType.getTypeClass(), to))
                .findFirst()
                .map(transformer -> transformer.transform(qilletniType))
                .orElseThrow(() -> new ListTransformerNotFoundException(String.format("No list transformer found from %s to %s", qilletniType.getTypeClass().getTypeName(), to.getTypeName())));
    }
    
    private record Transformer<T extends QilletniType, F extends QilletniType>(QilletniTypeClass<F> from, QilletniTypeClass<T> to, Function<F, T> transformerFunction) {
        public boolean matchesTypes(QilletniTypeClass<?> from, QilletniTypeClass<?> to) {
            return this.from.equals(from) && this.to.equals(to);
        }
        
        public T transform(QilletniType qilletniType) {
            return transformerFunction.apply((F) qilletniType);
        }
    }
}

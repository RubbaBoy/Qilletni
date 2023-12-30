package is.yarr.qilletni.lang.internal.adapter;

@FunctionalInterface
public interface TypeAdapter<R, T> {

    R convertType(T t);
    
    default R convertCastedType(Object t) {
        return convertType((T) t);
    }
    
}

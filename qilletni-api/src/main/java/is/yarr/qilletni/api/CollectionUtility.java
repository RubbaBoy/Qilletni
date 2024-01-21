package is.yarr.qilletni.api;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class CollectionUtility {
    
    public static <K, V> List<Entry<K, V>> getRecordEntries(Map<K, V> map) {
        return map.entrySet().stream().map(Entry::new).toList();
    }
    
    public static <T> List<T> createList(int size, T value) {
        return IntStream.range(0, size).mapToObj(i -> value).toList();
    }
    
    public record Entry<K, V>(K k, V v) {
        public Entry(Map.Entry<K, V> kvEntry) {
            this(kvEntry.getKey(), kvEntry.getValue());
        }
    }
}

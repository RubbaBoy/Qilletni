package is.yarr.qilletni;

import java.util.List;
import java.util.Map;

public class MapUtility {
    
    public static <K, V> List<Entry<K, V>> getRecordEntries(Map<K, V> map) {
        return map.entrySet().stream().map(Entry::new).toList();
    }
    
    public record Entry<K, V>(K k, V v) {
        public Entry(Map.Entry<K, V> kvEntry) {
            this(kvEntry.getKey(), kvEntry.getValue());
        }
    }
}

package dev.qilletni.api;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * A utility class for working with collections.
 */
public class CollectionUtility {

    /**
     * Gets a list of entries from a map.
     * 
     * @param map The map to get the entries from
     * @return The list of entries in the map
     * @param <K> The type of the keys in the map
     * @param <V> The type of the values in the map
     */
    public static <K, V> List<Entry<K, V>> getRecordEntries(Map<K, V> map) {
        return map.entrySet().stream().map(Entry::new).toList();
    }

    /**
     * Creates a list of a given size, filled with references of a given value.
     * 
     * @param size The size of the list to create
     * @param value The value to fill the list with
     * @return The list of references to the given value
     * @param <T> The type of the value to fill the list with
     */
    public static <T> List<T> createList(int size, T value) {
        return IntStream.range(0, size).mapToObj(i -> value).toList();
    }

    /**
     * A record representing a key-value pair.
     * 
     * @param k The key of the pair
     * @param v The value of the pair
     * @param <K> The type of the key
     * @param <V> The type of the value
     */
    public record Entry<K, V>(K k, V v) {
        public Entry(Map.Entry<K, V> kvEntry) {
            this(kvEntry.getKey(), kvEntry.getValue());
        }
    }
}

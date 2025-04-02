package dev.qilletni.api.lang.types.collection;

import java.util.Arrays;

/**
 * A backing type for the <code>order</code> type on a collection, e.g. <code>order[shuffle]</code> or
 * <code>order[sequential]</code>. This defines what order a collection should select songs from.
 */
public enum CollectionOrder {
    /**
     * The collection will select songs randomly.
     */
    SHUFFLE("shuffle"),

    /**
     * The collection will play songs in the order that it is defined.
     */
    SEQUENTIAL("sequential");
    
    private final String orderString;

    CollectionOrder(String orderString) {
        this.orderString = orderString;
    }

    /**
     * Gets the user-input string of the order.
     * 
     * @return The string definition of the order
     */
    public String getOrderString() {
        return orderString;
    }

    /**
     * Retrieves the {@link CollectionOrder} associated with the specified order string.
     *
     * @param orderString The string representation of the collection order
     * @return The {@link CollectionOrder} matching the specified string
     * @throws IllegalStateException If the specified string does not match any defined order
     */
    public static CollectionOrder getFromString(String orderString) {
        return Arrays.stream(values())
                .filter(order -> order.orderString.equals(orderString)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Unexpected value: " + orderString));
    }
}

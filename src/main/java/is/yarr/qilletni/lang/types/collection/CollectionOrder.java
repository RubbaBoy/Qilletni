package is.yarr.qilletni.lang.types.collection;

import java.util.Arrays;

public enum CollectionOrder {
    SHUFFLE("shuffle"),
    SEQUENTIAL("sequential");
    
    private final String orderString;

    CollectionOrder(String orderString) {
        this.orderString = orderString;
    }

    public String getOrderString() {
        return orderString;
    }
    
    public static CollectionOrder getFromString(String orderString) {
        return Arrays.stream(values())
                .filter(order -> order.orderString.equals(orderString)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Unexpected value: " + orderString));
    }
}

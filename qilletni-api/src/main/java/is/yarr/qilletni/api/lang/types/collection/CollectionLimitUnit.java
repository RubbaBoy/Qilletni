package is.yarr.qilletni.api.lang.types.collection;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public enum CollectionLimitUnit {
    COUNT("", null),
    SECOND("s", TimeUnit.SECONDS),
    MINUTE("m", TimeUnit.MINUTES),
    HOUR("h", TimeUnit.HOURS);
    
    private final String unitText;
    private final TimeUnit timeUnit;

    CollectionLimitUnit(String unitText, TimeUnit timeUnit) {
        this.unitText = unitText;
        this.timeUnit = timeUnit;
    }

    public String getUnitText() {
        return unitText;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public static CollectionLimitUnit fromText(String text) {
        return Arrays.stream(values())
                .filter(unit -> unit.unitText.equals(text)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Unexpected value: " + text));
    }
}

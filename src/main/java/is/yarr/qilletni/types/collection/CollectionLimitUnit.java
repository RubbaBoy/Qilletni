package is.yarr.qilletni.types.collection;

import is.yarr.qilletni.types.weights.WeightUnit;

import java.util.Arrays;

public enum CollectionLimitUnit {
    COUNT(""),
    SECOND("s"),
    MINUTE("m"),
    HOUR("h");
    
    private final String unitText;

    CollectionLimitUnit(String unitText) {
        this.unitText = unitText;
    }

    public String getUnitText() {
        return unitText;
    }
    
    public static CollectionLimitUnit fromText(String text) {
        return Arrays.stream(values())
                .filter(unit -> unit.unitText.equals(text)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Unexpected value: " + text));
    }
}

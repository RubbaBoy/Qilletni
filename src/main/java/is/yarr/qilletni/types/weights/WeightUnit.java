package is.yarr.qilletni.types.weights;

import java.util.Arrays;

public enum WeightUnit {
    PERCENT("%"),
    MULTIPLIER("x");
    
    private final String stringUnit;

    WeightUnit(String stringUnit) {
        this.stringUnit = stringUnit;
    }

    public String getStringUnit() {
        return stringUnit;
    }
    
    public static WeightUnit fromSymbol(String text) {
        return Arrays.stream(values())
                .filter(unit -> unit.stringUnit.equals(text)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Unexpected value: " + text));
    }
}

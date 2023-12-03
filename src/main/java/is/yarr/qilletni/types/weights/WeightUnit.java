package is.yarr.qilletni.types.weights;

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
        return switch (text) {
            case "x" -> MULTIPLIER;
            case "%" -> PERCENT;
            default -> throw new IllegalStateException("Unexpected value: " + text);
        };
    }
}

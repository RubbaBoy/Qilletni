package dev.qilletni.api.lang.types.weights;

import java.util.Arrays;

/**
 * The unit of a {@link WeightEntry}, which helps determine how the weight is calculated. This unit is associated with
 * a number also in the weight entry.
 */
public enum WeightUnit {

    /**
     * The weight will be chosen a given percent of time every song is chosen from the collection playing the weight.
     * If all the weight entries in a <code>weights</code> expression have this unit, the percent must add up to exactly
     * 100%.
     */
    PERCENT("%"),

    /**
     * The weight, which must represent a track, will show up a given number of times in the collection playing the
     * weights, then played or shuffled as normal. This simply lets a track be played more often in a playlist than it
     * usually would.
     */
    MULTIPLIER("x");
    
    private final String stringUnit;

    WeightUnit(String stringUnit) {
        this.stringUnit = stringUnit;
    }

    /**
     * Gets the string after the number of the weight unit, either <code>"%"</code> or <code>"x"</code>. 
     * 
     * @return The string unit of the weight
     */
    public String getStringUnit() {
        return stringUnit;
    }

    /**
     * Gets the {@link WeightUnit} that would be returned by {@link #getStringUnit()}. An exception is thrown if it is
     * not either <code>"%"</code> or <code>"x"</code>.
     * 
     * @param text The string unit of the weight, either <code>"%"</code> or <code>"x"</code>
     * @return The {@link WeightUnit} found
     */
    public static WeightUnit fromSymbol(String text) {
        return Arrays.stream(values())
                .filter(unit -> unit.stringUnit.equals(text)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Unexpected value: " + text));
    }
}

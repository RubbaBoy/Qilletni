package is.yarr.qilletni.api.lang.types.weights;

import is.yarr.qilletni.api.music.supplier.TrackSupplier;

/**
 * Represents a line in a <code>weights</code> expression. Each entry is something that can supply a track, as it
 * implements {@link TrackSupplier}. This also holds additional options for the weight, such as the amount and unit.
 * Weight entries all begin with a separator, either <code>|</code> <code>|!</code> or <code>|~</code>, each providing
 * different behavior. See the <a href="https://qilletni.yarr.is/language/types/built_in_types/#weight-separators">Weight Separator</a> docs for more info.
 */
public interface WeightEntry extends TrackSupplier {

    /**
     * Gets the amount of weight for this entry. This pairs with the unit from {@link #getWeightUnit()}. If the unit is
     * {@link WeightUnit#MULTIPLIER}, the track will be played this many times more than normal in the collection. If it
     * is a {@link WeightUnit#PERCENT}, it will play the track this number of percent of times any track is played.
     * 
     * @return The weight amount
     */
    double getWeightAmount();

    /**
     * Sets the weight amount from {@link #getWeightAmount()}.
     * 
     * @param weightAmount The amount to set the weight to
     */
    void setWeightAmount(double weightAmount);

    /**
     * Gets the unit this weight entry was assigned, through the syntax of <code>12x</code> or <code>12%</code>.
     * 
     * @return The unit of the weight entry
     */
    WeightUnit getWeightUnit();

    /**
     * Sets the weight unit from {@link #getWeightUnit()}.
     * 
     * @param weightUnit The unit of the weight entry
     */
    void setWeightUnit(WeightUnit weightUnit);

    /**
     * If the weight unit can repeat tracks. This is true if the separator used is <code>|!</code>
     * 
     * @param canRepeatTrack If the track can be repeated
     */
    void setCanRepeat(boolean canRepeatTrack);

    /**
     * Gets if the weight entry can repeat tracks. This is true if the separator used is <code>|!</code>
     * 
     * @return If the track can be repeated
     */
    boolean getCanRepeatTrack();

    /**
     * Sets if the weight entry can repeat if chosen twice in a row while playing a collection. This is true if the separator used is <code>|</code> or <code>|!</code>
     * 
     * @param canRepeatWeight If the weight entry can be repeated
     */
    void setCanRepeatWeight(boolean canRepeatWeight);

    /**
     * Gets if the weight entry can repeat if chosen twice in a row while playing a collection. This is true if the separator used is <code>|</code> or <code>|!</code>
     * 
     * @return If the weight entry can be repeated
     */
    boolean getCanRepeatWeight();

    /**
     * Gets what supplier the weight entry uses, in the form of the {@link WeightTrackType}.
     * 
     * @return The type of track supplier
     */
    WeightTrackType getTrackType();

    /**
     * Gets a string representation of the track supplier. This is typically only used for logging or display purposes.
     * It may not include all information about the track supplier.
     * 
     * @return A string representation of the track supplier
     */
    String getTrackStringValue();
}

package is.yarr.qilletni.api.lang.types.weights;

import is.yarr.qilletni.api.music.supplier.TrackSupplier;

public interface WeightEntry extends TrackSupplier {
    double getWeightAmount();

    void setWeightAmount(double weightAmount);

    WeightUnit getWeightUnit();

    void setWeightUnit(WeightUnit weightUnit);

    void setCanRepeat(boolean canRepeatTrack);
    
    boolean getCanRepeatTrack();
    
    void setCanRepeatWeight(boolean canRepeatWeight);
    
    boolean getCanRepeatWeight();
    
    WeightTrackType getTrackType();
    
    String getTrackStringValue();
}

package is.yarr.qilletni.api.lang.types.weights;

import is.yarr.qilletni.api.lang.types.SongType;

public interface WeightEntry {
    int getWeightAmount();

    void setWeightAmount(int weightAmount);

    WeightUnit getWeightUnit();

    void setWeightUnit(WeightUnit weightUnit);

    SongType getSong();

    void setSong(SongType song);
    
    void setCanRepeat(boolean canRepeat);
    
    boolean getCanRepeat();
}

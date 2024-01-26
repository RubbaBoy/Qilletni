package is.yarr.qilletni.lang.types.weights;

import is.yarr.qilletni.api.lang.types.weights.WeightEntry;
import is.yarr.qilletni.api.lang.types.weights.WeightUnit;
import is.yarr.qilletni.api.lang.types.SongType;

public class WeightEntryImpl implements WeightEntry {
    private int weightAmount;
    private WeightUnit weightUnit;
    private SongType song;
    private boolean canRepeat;

    public WeightEntryImpl(int weightAmount, WeightUnit weightUnit, SongType song, boolean canRepeat) {
        this.weightAmount = weightAmount;
        this.weightUnit = weightUnit;
        this.song = song;
        this.canRepeat = canRepeat;
    }

    @Override
    public int getWeightAmount() {
        return weightAmount;
    }

    @Override
    public void setWeightAmount(int weightAmount) {
        this.weightAmount = weightAmount;
    }

    @Override
    public WeightUnit getWeightUnit() {
        return weightUnit;
    }

    @Override
    public void setWeightUnit(WeightUnit weightUnit) {
        this.weightUnit = weightUnit;
    }

    @Override
    public SongType getSong() {
        return song;
    }

    @Override
    public void setSong(SongType song) {
        this.song = song;
    }

    @Override
    public void setCanRepeat(boolean canRepeat) {
        this.canRepeat = canRepeat;
    }

    @Override
    public boolean getCanRepeat() {
        return canRepeat;
    }

    @Override
    public String toString() {
        return "WeightEntry[" + weightAmount + weightUnit.getStringUnit() + " " + song + "]";
    }
}

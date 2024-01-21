package is.yarr.qilletni.lang.types.weights;

import is.yarr.qilletni.api.lang.types.weights.WeightEntry;
import is.yarr.qilletni.api.music.orchestrator.weights.WeightUnit;
import is.yarr.qilletni.api.lang.types.SongType;

public class WeightEntryImpl implements WeightEntry {
    private int weightAmount;
    private WeightUnit weightUnit;
    private SongType song;

    public WeightEntryImpl(int weightAmount, WeightUnit weightUnit, SongType song) {
        this.weightAmount = weightAmount;
        this.weightUnit = weightUnit;
        this.song = song;
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
    public String toString() {
        return "WeightEntry[" + weightAmount + weightUnit.getStringUnit() + " " + song + "]";
    }
}

package is.yarr.qilletni.lang.types.weights;

import is.yarr.qilletni.api.music.orchestrator.weights.WeightUnit;
import is.yarr.qilletni.lang.types.SongType;
import is.yarr.qilletni.api.music.orchestrator.weights.WeightUnit;

public class WeightEntry {
    private int weightAmount;
    private WeightUnit weightUnit;
    private SongType song;

    public WeightEntry(int weightAmount, WeightUnit weightUnit, SongType song) {
        this.weightAmount = weightAmount;
        this.weightUnit = weightUnit;
        this.song = song;
    }

    public int getWeightAmount() {
        return weightAmount;
    }

    public void setWeightAmount(int weightAmount) {
        this.weightAmount = weightAmount;
    }

    public WeightUnit getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(WeightUnit weightUnit) {
        this.weightUnit = weightUnit;
    }

    public SongType getSong() {
        return song;
    }

    public void setSong(SongType song) {
        this.song = song;
    }

    @Override
    public String toString() {
        return "WeightEntry[" + weightAmount + weightUnit.getStringUnit() + " " + song + "]";
    }
}

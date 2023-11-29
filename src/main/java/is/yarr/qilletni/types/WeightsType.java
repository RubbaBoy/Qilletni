package is.yarr.qilletni.types;

public final class WeightsType implements QilletniType {
    
    class WeightEntry {
        private int weightAmount;
        private WeightUnit weightUnit;
        private SongType song;
    }
    
    enum WeightUnit {
        PERCENT,
        MULTIPLIER
    }
    
    @Override
    public String stringValue() {
        return "~weights~";
    }

    @Override
    public String typeName() {
        return "weights";
    }
}

package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.api.lang.types.WeightsType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.api.lang.types.weights.WeightEntry;

import java.util.List;

public final class WeightsTypeImpl implements WeightsType {
    
    private final List<WeightEntry> weightEntries;

    public WeightsTypeImpl(List<WeightEntry> weightEntries) {
        this.weightEntries = weightEntries;
    }

    @Override
    public List<WeightEntry> getWeightEntries() {
        return weightEntries;
    }

    @Override
    public String stringValue() {
        return String.format("weights[%s]", String.join(", ", weightEntries.stream()
                .map(entry -> String.format("%.2f%s %s",
                        entry.getWeightAmount(),
                        entry.getWeightUnit().getStringUnit(),
                        entry.getTrackStringValue()))
                .toList()));
    }

    @Override
    public QilletniTypeClass<WeightsType> getTypeClass() {
        return QilletniTypeClass.WEIGHTS;
    }

    @Override
    public String toString() {
        return "WeightsType{" +
                "weights=" + weightEntries +
                '}';
    }
}

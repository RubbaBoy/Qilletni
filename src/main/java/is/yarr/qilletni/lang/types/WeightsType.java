package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.lang.types.weights.WeightEntry;

import java.util.List;

public final class WeightsType extends QilletniType {
    
    private final List<WeightEntry> weightEntries;

    public WeightsType(List<WeightEntry> weightEntries) {
        this.weightEntries = weightEntries;
    }

    public List<WeightEntry> getWeightEntries() {
        return weightEntries;
    }

    @Override
    public String stringValue() {
        return String.format("weights[%s]", String.join(", ", weightEntries.stream()
                .map(entry -> String.format("%d%s %s",
                        entry.getWeightAmount(),
                        entry.getWeightUnit().getStringUnit(),
                        entry.getSong().stringValue()))
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

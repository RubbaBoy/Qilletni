package is.yarr.qilletni.types;

import is.yarr.qilletni.types.weights.WeightEntry;

import java.util.List;

public final class WeightsType implements QilletniType {
    
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
    public String typeName() {
        return "weights";
    }

    @Override
    public String toString() {
        return "WeightsType{" +
                "weights=" + weightEntries +
                '}';
    }
}

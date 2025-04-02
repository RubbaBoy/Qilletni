package dev.qilletni.impl.lang.types;

import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.lang.types.WeightsType;
import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import dev.qilletni.api.lang.types.weights.WeightEntry;
import dev.qilletni.impl.lang.exceptions.UnsupportedOperatorException;

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
    public QilletniType plusOperator(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "+");
    }

    @Override
    public void plusOperatorInPlace(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "+");
    }

    @Override
    public QilletniType minusOperator(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "-");
    }

    @Override
    public void minusOperatorInPlace(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "-");
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

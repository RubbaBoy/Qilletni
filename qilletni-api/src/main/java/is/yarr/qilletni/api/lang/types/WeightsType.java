package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.types.weights.WeightEntry;

import java.util.List;

public non-sealed interface WeightsType extends QilletniType {
    List<WeightEntry> getWeightEntries();
}

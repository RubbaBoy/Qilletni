package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.types.weights.WeightEntry;

import java.util.List;

/**
 * A Qilletni type representing weights that may be applied to a collection. Weights manipulate the way a collection is
 * played.
 */
public non-sealed interface WeightsType extends AnyType {

    /**
     * Gets all the entries specified in the weights.
     * 
     * @return The entries in the weights type
     */
    List<WeightEntry> getWeightEntries();
}

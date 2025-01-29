package is.yarr.qilletni.api.lang.types.weights;

import is.yarr.qilletni.api.exceptions.InvalidWeightException;
import is.yarr.qilletni.api.lang.types.WeightsType;

/**
 * Utilities to assist in weight selection.
 */
public class WeightUtils {

    /**
     * Checks if the weights are valid, meaning they don't exceed a total of 100%
     *
     * @param weights The weights to validate
     * @return The total weight percent
     */
    public static double validateWeights(WeightsType weights) {
        if (weights == null) {
            return 0;
        }

        double totalPercent = weights.getWeightEntries().stream()
                .filter(entry -> entry.getWeightUnit() == WeightUnit.PERCENT)
                .map(WeightEntry::getWeightAmount)
                .reduce(Double::sum)
                .orElse(0D);

        if (totalPercent > 100) {
            throw new InvalidWeightException("Total weight percentage cannot go over 100%! Current total is " + totalPercent + "%");
        }
        
        return totalPercent;
    }
    
}

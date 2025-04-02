package dev.qilletni.impl.music.orchestration;

import dev.qilletni.api.lang.types.WeightsType;
import dev.qilletni.api.lang.types.weights.WeightEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class WeightDispersion {

    private final List<DispersedWeightEntry> scaledWeights;
    private final int scalingFactor;

    WeightDispersion(List<DispersedWeightEntry> scaledWeights, int scalingFactor) {
        this.scaledWeights = scaledWeights;
        this.scalingFactor = scalingFactor;
    }
    
    public static WeightDispersion initializeWeightDispersion(WeightsType weightsType) {
        if (weightsType == null) {
            return new EmptyWeightDispersion();
        }
        
        int scalingFactor = findScalingFactor(weightsType.getWeightEntries());
        return new WeightDispersion(scaleAndCumulateWeights(scalingFactor, weightsType.getWeightEntries()), scalingFactor);
    }

    /**
     * Selects a {@link WeightEntry} based off of dispersed weights.
     * 
     * @return A found {@link WeightEntry}, if any
     */
    public Optional<WeightEntry> selectWeight() {
        return weightedChoice(100 * scalingFactor, scaledWeights);
    }

    private static int findScalingFactor(List<WeightEntry> weights) {
        int maxDecimalPlaces = 0;
        for (var weightEntry : weights) {
            String weightStr = Double.toString(weightEntry.getWeightAmount());
            int decimalPlaces = weightStr.length() - weightStr.indexOf('.') - 1;
            maxDecimalPlaces = Math.max(maxDecimalPlaces, decimalPlaces);
        }
        return (int) Math.pow(10, maxDecimalPlaces);
    }

    private static List<DispersedWeightEntry> scaleAndCumulateWeights(int scalingFactor, List<WeightEntry> weightEntries) {
        int cumulative = 0;
        var scaledWeights = new ArrayList<DispersedWeightEntry>();

        for (var weightEntry : weightEntries) {
            cumulative += (int) (weightEntry.getWeightAmount() * scalingFactor);
            scaledWeights.add(new DispersedWeightEntry(weightEntry, cumulative));
        }

        return scaledWeights;
    }

    private Optional<WeightEntry> weightedChoice(int maxRange, List<DispersedWeightEntry> cumulativeWeights) {
        if (cumulativeWeights.isEmpty()) {
            return Optional.empty();
        }

        int randomNumber = ThreadLocalRandom.current().nextInt(maxRange);
        return binarySearch(cumulativeWeights, randomNumber);
    }

    private Optional<WeightEntry> binarySearch(List<DispersedWeightEntry> cumulativeWeights, int target) {
        int low = 0, high = cumulativeWeights.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            var midVal = cumulativeWeights.get(mid);
            if (midVal.cumulativeWeight < target) {
                low = mid + 1;
            } else if (mid > 0 && cumulativeWeights.get(mid - 1).cumulativeWeight >= target) {
                high = mid - 1;
            } else {
                return Optional.ofNullable(midVal.weightEntry());
            }
        }
        
        return Optional.empty();
    }
    
    record DispersedWeightEntry(WeightEntry weightEntry, int cumulativeWeight) {}

    @Override
    public String toString() {
        return "WeightDispersion{scaledWeights=%s, scalingFactor=%d}".formatted(scaledWeights, scalingFactor);
    }
}

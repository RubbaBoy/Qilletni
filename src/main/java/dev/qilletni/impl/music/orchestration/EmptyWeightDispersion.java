package dev.qilletni.impl.music.orchestration;

import dev.qilletni.api.lang.types.weights.WeightEntry;

import java.util.Optional;

public class EmptyWeightDispersion extends WeightDispersion {

    EmptyWeightDispersion() {
        super(null, 0);
    }

    @Override
    public Optional<WeightEntry> selectWeight() {
        return Optional.empty();
    }
}

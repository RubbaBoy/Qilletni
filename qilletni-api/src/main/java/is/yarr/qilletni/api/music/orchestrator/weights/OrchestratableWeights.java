package is.yarr.qilletni.api.music.orchestrator.weights;

import is.yarr.qilletni.api.music.Track;

public class OrchestratableWeights {
    
    record WeightEntry(int amount, WeightUnit unit, Track track) {}
    
}

package is.yarr.qilletni.api.music.orchestrator;

import is.yarr.qilletni.api.music.PlayActor;
import is.yarr.qilletni.api.music.TrackOrchestrator;

public class DefaultTrackOrchestrator implements TrackOrchestrator {

    private final PlayActor playActor;

    public DefaultTrackOrchestrator(PlayActor playActor) {
        this.playActor = playActor;
    }
    
}

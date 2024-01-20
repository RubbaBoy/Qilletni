package is.yarr.qilletni.music.orchestrator;

import is.yarr.qilletni.music.PlayActor;
import is.yarr.qilletni.music.TrackOrchestrator;

public class DefaultTrackOrchestrator implements TrackOrchestrator {

    private final PlayActor playActor;

    public DefaultTrackOrchestrator(PlayActor playActor) {
        this.playActor = playActor;
    }
    
}

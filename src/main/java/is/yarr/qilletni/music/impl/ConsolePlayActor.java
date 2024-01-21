package is.yarr.qilletni.music.impl;

import is.yarr.qilletni.api.music.PlayActor;
import is.yarr.qilletni.api.music.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class ConsolePlayActor implements PlayActor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsolePlayActor.class);
    
    @Override
    public CompletableFuture<PlayResult> playTrack(Track track) {
        LOGGER.info("Playing: {} - {}", track.getName(), track.getArtist().getName());
        
        return CompletableFuture.completedFuture(PlayResult.SUCCESS);
    }
}

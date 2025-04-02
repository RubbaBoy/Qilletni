package dev.qilletni.api.music;

import java.util.concurrent.CompletableFuture;

public interface PlayActor {
    
    CompletableFuture<PlayResult> playTrack(Track track);
    
    enum PlayResult {
        SUCCESS,
        ERROR
    }
    
}

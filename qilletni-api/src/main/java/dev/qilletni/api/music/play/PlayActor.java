package dev.qilletni.api.music.play;

import dev.qilletni.api.music.Track;

import java.util.concurrent.CompletableFuture;

public interface PlayActor {
    
    CompletableFuture<PlayResult> playTrack(Track track);
    
    enum PlayResult {
        SUCCESS,
        ERROR
    }
    
}

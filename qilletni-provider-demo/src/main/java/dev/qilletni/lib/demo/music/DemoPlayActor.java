package dev.qilletni.lib.demo.music;

import dev.qilletni.api.music.PlayActor;
import dev.qilletni.api.music.Track;

import java.util.concurrent.CompletableFuture;

public class DemoPlayActor implements PlayActor {
    
    @Override
    public CompletableFuture<PlayResult> playTrack(Track track) {
        System.out.println("Demo playing: " + track);
        
        return CompletableFuture.completedFuture(PlayResult.SUCCESS);
    }
}

package dev.qilletni.api.music;

import dev.qilletni.api.music.play.PlayActor;

import java.util.concurrent.CompletableFuture;

public class ConsolePlayActor implements PlayActor {
    
    @Override
    public CompletableFuture<PlayResult> playTrack(Track track) {
        System.out.println("Playing: " + track.getName() + " - " +  track.getArtist().getName());
        
        return CompletableFuture.completedFuture(PlayResult.SUCCESS);
    }
}

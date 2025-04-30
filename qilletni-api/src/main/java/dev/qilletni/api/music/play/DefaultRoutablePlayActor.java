package dev.qilletni.api.music.play;

import dev.qilletni.api.music.Track;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * This is a sample base implementation of a {@link PlayActor} that can be routed to different {@link PlayActor}.
 * Service providers that use this implementation can use the standard library's <code>play_redirect.ql</code> to
 * switch between what this is routed to.
 */
public class DefaultRoutablePlayActor implements PlayActor {

    private final PlayActor defaultPlayActor;
    private Function<Track, CompletableFuture<PlayActor.PlayResult>> reroutedPlayTrack;
    
    public DefaultRoutablePlayActor(PlayActor defaultPlayActor) {
        this.defaultPlayActor = defaultPlayActor;
        this.reroutedPlayTrack = defaultPlayActor::playTrack;
    }

    @Override
    public CompletableFuture<PlayActor.PlayResult> playTrack(Track track) {
        return reroutedPlayTrack.apply(track);
    }

    /**
     * Sets the function that will be used to play a track.
     * 
     * @param playFunction The function to use to play a track
     */
    public void setReroutedPlayTrack(Function<Track, CompletableFuture<PlayActor.PlayResult>> playFunction) {
        reroutedPlayTrack = playFunction;
    }

    /**
     * Resets the function that will be used to play a track. This will use the default {@link PlayActor}.
     */
    public void resetReroutedPlayTrack() {
        reroutedPlayTrack = defaultPlayActor::playTrack;
    }

    /**
     * Gets the default {@link PlayActor}.
     * 
     * @return The default {@link PlayActor}
     */
    public PlayActor getDefaultPlay() {
        return defaultPlayActor;
    }
}

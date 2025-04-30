package dev.qilletni.api.music.play;

import dev.qilletni.api.music.Track;

import java.util.concurrent.CompletableFuture;

/**
 * An actor that can play a track.
 */
public interface PlayActor {

    /**
     * Plays a track. This is highly implementation-dependent.
     *
     * @param track The track to play
     * @return The result of the play operation. Some implementations may return immediately, while others may wait for
     * the track to finish playing.
     */
    CompletableFuture<PlayResult> playTrack(Track track);

    /**
     * The result of a {@link PlayActor#playTrack(Track)} operation.
     */
    enum PlayResult {
        /**
         * The track was successfully played.
         */
        SUCCESS,

        /**
         * There was an error playing the track.
         */
        ERROR
    }

}

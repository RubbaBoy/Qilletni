package is.yarr.qilletni.music.spotify.auth;

import se.michaelthelin.spotify.SpotifyApi;

import java.util.concurrent.CompletableFuture;

public interface SpotifyAuthorizer {

    /**
     * Creates an authorized {@link SpotifyApi}, with an automatic refresh loop (if applicable).
     * 
     * @return The future of the created {@link SpotifyApi}
     */
    CompletableFuture<SpotifyApi> authorizeSpotify();

    /**
     * Gets the current {@link SpotifyApi} after authorization.
     * 
     * @return The current {@link SpotifyApi}
     */
    SpotifyApi getSpotifyApi();
    
}

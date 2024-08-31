package is.yarr.qilletni.music.spotify.auth;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.User;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface SpotifyAuthorizer {

    /**
     * Creates an authorized {@link SpotifyApi}, with an automatic refresh loop (if applicable).
     * 
     * @return The future of the created {@link SpotifyApi}
     */
    CompletableFuture<SpotifyApi> authorizeSpotify();

    /**
     * Shuts down the authorizer. This should clean up any async tasks currently running.
     */
    void shutdown();

    /**
     * Gets the current {@link SpotifyApi} after authorization.
     * 
     * @return The current {@link SpotifyApi}
     */
    SpotifyApi getSpotifyApi();

    /**
     * Gets the current user profile that has been authenticated with. If no user is associated with the
     * authentication, an empty optional is returned.
     * 
     * @return The current user
     */
    Optional<User> getCurrentUser();
}

package is.yarr.qilletni.api.auth;

import is.yarr.qilletni.api.music.MusicCache;
import is.yarr.qilletni.api.music.MusicFetcher;

import java.util.concurrent.CompletableFuture;

public interface ServiceProvider {

    /**
     * Initializes the service provider, and returns the created {@link MusicCache}. This populates
     * {@link #getMusicCache()} and {@link #getMusicFetcher()}.
     * 
     * @return The created {@link MusicCache} upon completion
     */
    CompletableFuture<MusicCache> initialize();

    /**
     * Gets the name of the provider.
     * 
     * @return The provider's name
     */
    String getName();

    /**
     * The {@link MusicCache} created after initialization.
     * 
     * @return The created {@link MusicCache}
     */
    MusicCache getMusicCache();

    /**
     * The {@link MusicFetcher} created after initialization.
     *
     * @return The created {@link MusicFetcher}
     */
    MusicFetcher getMusicFetcher();
    
}

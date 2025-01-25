package is.yarr.qilletni.api.auth;

import is.yarr.qilletni.api.lib.persistence.PackageConfig;
import is.yarr.qilletni.api.music.MusicCache;
import is.yarr.qilletni.api.music.MusicFetcher;
import is.yarr.qilletni.api.music.MusicTypeConverter;
import is.yarr.qilletni.api.music.PlayActor;
import is.yarr.qilletni.api.music.StringIdentifier;
import is.yarr.qilletni.api.music.factories.AlbumTypeFactory;
import is.yarr.qilletni.api.music.factories.CollectionTypeFactory;
import is.yarr.qilletni.api.music.factories.SongTypeFactory;
import is.yarr.qilletni.api.music.orchestration.TrackOrchestrator;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * A service provider interface that facilitates the management of music-related resources and operations, including
 * caching, fetching, orchestration, and type conversion. This may be switched by the Qilletni program through the use
 * of a {@link is.yarr.qilletni.api.music.supplier.DynamicProvider}.
 */
public interface ServiceProvider {

    /**
     * Initializes the service provider. This populates {@link #getMusicCache()}, {@link #getMusicFetcher()},
     * and {@link #getTrackOrchestrator()}.
     *
     * @param defaultTrackOrchestratorFunction If the service provider doesn't implement a custom,
     *                                         {@link TrackOrchestrator}, it should run this function to create the
     *                                         default implementation of one and use it.
     * @param packageConfig The {@link PackageConfig} for the service provider. This provides persistent storage for
     *                      the package. Use {@link PackageConfig#loadConfig()} to load the configuration.
     * @return The created future of the initialization
     */
    CompletableFuture<Void> initialize(BiFunction<PlayActor, MusicCache, TrackOrchestrator> defaultTrackOrchestratorFunction, PackageConfig packageConfig);

    /**
     * Shuts down the service provider. This should clean up any async tasks currently running.
     */
    void shutdown();

    /**
     * Gets the name of the provider.
     *
     * @return The provider's name
     */
    String getName();

    /**
     * Gets the {@link MusicCache} created after initialization.
     *
     * @return The created {@link MusicCache}
     */
    MusicCache getMusicCache();

    /**
     * Gets the {@link MusicFetcher} created after initialization.
     *
     * @return The created {@link MusicFetcher}
     */
    MusicFetcher getMusicFetcher();

    /**
     * Gets the {@link TrackOrchestrator} created after initialization.
     *
     * @return The created {@link TrackOrchestrator}
     */
    TrackOrchestrator getTrackOrchestrator();

    /**
     * Gets the {@link MusicTypeConverter} created after initialization.
     * 
     * @return The created {@link MusicTypeConverter}
     */
    MusicTypeConverter getMusicTypeConverter();

    /**
     * Gets the {@link StringIdentifier}.
     *
     * @return The created {@link StringIdentifier}
     */
    StringIdentifier getStringIdentifier(SongTypeFactory songTypeFactory, CollectionTypeFactory collectionTypeFactory, AlbumTypeFactory albumTypeFactory);
}

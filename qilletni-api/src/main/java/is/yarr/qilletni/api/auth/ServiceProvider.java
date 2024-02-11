package is.yarr.qilletni.api.auth;

import is.yarr.qilletni.api.music.MusicCache;
import is.yarr.qilletni.api.music.MusicFetcher;
import is.yarr.qilletni.api.music.PlayActor;
import is.yarr.qilletni.api.music.StringIdentifier;
import is.yarr.qilletni.api.music.orchestration.TrackOrchestrator;
import is.yarr.qilletni.api.music.factories.AlbumTypeFactory;
import is.yarr.qilletni.api.music.factories.CollectionTypeFactory;
import is.yarr.qilletni.api.music.factories.SongTypeFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ServiceProvider {

    /**
     * Initializes the service provider. This populates {@link #getMusicCache()}, {@link #getMusicFetcher()},
     * and {@link #getTrackOrchestrator()}.
     *
     * @param defaultTrackOrchestratorFunction If the service provider doesn't implement a custom,
     *                                         {@link TrackOrchestrator}, it should run this function to create the
     *                                         default implementation of one and use it.
     * @return The created future of the initialization
     */
    CompletableFuture<Void> initialize(BiFunction<PlayActor, MusicCache, TrackOrchestrator> defaultTrackOrchestratorFunction);

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
     * Gets the {@link StringIdentifier}.
     *
     * @return The created {@link StringIdentifier}
     */
    StringIdentifier getStringIdentifier(SongTypeFactory songTypeFactory, CollectionTypeFactory collectionTypeFactory, AlbumTypeFactory albumTypeFactory);
}

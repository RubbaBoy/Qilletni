package dev.qilletni.lib.demo.music.provider;

import dev.qilletni.api.auth.ServiceProvider;
import dev.qilletni.api.music.MusicCache;
import dev.qilletni.api.music.MusicFetcher;
import dev.qilletni.api.music.PlayActor;
import dev.qilletni.api.music.StringIdentifier;
import dev.qilletni.api.music.factories.AlbumTypeFactory;
import dev.qilletni.api.music.factories.CollectionTypeFactory;
import dev.qilletni.api.music.factories.SongTypeFactory;
import dev.qilletni.api.music.orchestration.TrackOrchestrator;
import dev.qilletni.lib.demo.music.DemoMusicCache;
import dev.qilletni.lib.demo.music.DemoMusicFetcher;
import dev.qilletni.lib.demo.music.DemoPlayActor;
import dev.qilletni.lib.demo.music.DemoStringIdentifier;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class DemoServiceProvider implements ServiceProvider {
    
    private DemoMusicCache musicCache;
    private MusicFetcher musicFetcher;
    private TrackOrchestrator trackOrchestrator;
    private StringIdentifier stringIdentifier;
    
    @Override
    public CompletableFuture<Void> initialize(BiFunction<PlayActor, MusicCache, TrackOrchestrator> defaultTrackOrchestratorFunction) {
        var demoMusicFetcher = new DemoMusicFetcher();
        musicFetcher = demoMusicFetcher;
        musicCache = new DemoMusicCache(demoMusicFetcher);
        trackOrchestrator = defaultTrackOrchestratorFunction.apply(new DemoPlayActor(), musicCache);
        
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "Demo";
    }

    @Override
    public MusicCache getMusicCache() {
        return Objects.requireNonNull(musicCache, "ServiceProvider#initialize must be invoked to initialize MusicCache");
    }

    @Override
    public MusicFetcher getMusicFetcher() {
        return Objects.requireNonNull(musicFetcher, "ServiceProvider#initialize must be invoked to initialize MusicFetcher");
    }

    @Override
    public TrackOrchestrator getTrackOrchestrator() {
        return Objects.requireNonNull(trackOrchestrator, "ServiceProvider#initialize must be invoked to initialize TrackOrchestrator");
    }

    @Override
    public StringIdentifier getStringIdentifier(SongTypeFactory songTypeFactory, CollectionTypeFactory collectionTypeFactory, AlbumTypeFactory albumTypeFactory) {
        if (stringIdentifier == null) {
            return stringIdentifier = new DemoStringIdentifier(musicCache, songTypeFactory, collectionTypeFactory, albumTypeFactory);
        }

        return stringIdentifier;
    }
}

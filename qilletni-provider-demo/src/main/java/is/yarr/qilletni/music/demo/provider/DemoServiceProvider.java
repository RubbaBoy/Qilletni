package is.yarr.qilletni.music.demo.provider;

import is.yarr.qilletni.api.auth.ServiceProvider;
import is.yarr.qilletni.api.music.ConsolePlayActor;
import is.yarr.qilletni.api.music.MusicCache;
import is.yarr.qilletni.api.music.MusicFetcher;
import is.yarr.qilletni.api.music.PlayActor;
import is.yarr.qilletni.api.music.StringIdentifier;
import is.yarr.qilletni.api.music.factories.AlbumTypeFactory;
import is.yarr.qilletni.api.music.factories.CollectionTypeFactory;
import is.yarr.qilletni.api.music.factories.SongTypeFactory;
import is.yarr.qilletni.api.music.orchestration.TrackOrchestrator;
import is.yarr.qilletni.music.demo.DemoMusicCache;
import is.yarr.qilletni.music.demo.DemoMusicFetcher;
import is.yarr.qilletni.music.demo.DemoPlayActor;
import is.yarr.qilletni.music.demo.DemoStringIdentifier;

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

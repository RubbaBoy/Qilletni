package is.yarr.qilletni.music.spotify.provider;

import is.yarr.qilletni.api.auth.ServiceProvider;
import is.yarr.qilletni.api.music.MusicCache;
import is.yarr.qilletni.api.music.MusicFetcher;
import is.yarr.qilletni.api.music.PlayActor;
import is.yarr.qilletni.api.music.StringIdentifier;
import is.yarr.qilletni.api.music.orchestration.TrackOrchestrator;
import is.yarr.qilletni.api.music.factories.AlbumTypeFactory;
import is.yarr.qilletni.api.music.factories.CollectionTypeFactory;
import is.yarr.qilletni.api.music.factories.SongTypeFactory;
import is.yarr.qilletni.music.spotify.SpotifyMusicCache;
import is.yarr.qilletni.music.spotify.SpotifyMusicFetcher;
import is.yarr.qilletni.music.spotify.SpotifyStringIdentifier;
import is.yarr.qilletni.music.spotify.auth.SpotifyApiSingleton;
import is.yarr.qilletni.music.spotify.auth.SpotifyAuthorizer;
import is.yarr.qilletni.music.spotify.auth.pkce.SpotifyPKCEAuthorizer;
import is.yarr.qilletni.music.spotify.play.ReroutablePlayActor;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class SpotifyServiceProvider implements ServiceProvider {
    
    private SpotifyMusicCache musicCache;
    private MusicFetcher musicFetcher;
    private TrackOrchestrator trackOrchestrator;
    private StringIdentifier stringIdentifier;
    
    @Override
    public CompletableFuture<Void> initialize(BiFunction<PlayActor, MusicCache, TrackOrchestrator> defaultTrackOrchestratorFunction) {
        SpotifyAuthorizer authorizer = SpotifyPKCEAuthorizer.createWithCodes();
        
        return authorizer.authorizeSpotify().thenRun(() -> {
            var spotifyMusicFetcher = new SpotifyMusicFetcher(authorizer);
            SpotifyApiSingleton.setSpotifyAuthorizer(authorizer);
            musicFetcher = spotifyMusicFetcher;
            musicCache = new SpotifyMusicCache(spotifyMusicFetcher);
            trackOrchestrator = defaultTrackOrchestratorFunction.apply(new ReroutablePlayActor(), musicCache);
        });
    }

    @Override
    public String getName() {
        return "Spotify";
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
            return stringIdentifier = new SpotifyStringIdentifier(musicCache, songTypeFactory, collectionTypeFactory, albumTypeFactory);
        }
        
        return stringIdentifier;
    }
}

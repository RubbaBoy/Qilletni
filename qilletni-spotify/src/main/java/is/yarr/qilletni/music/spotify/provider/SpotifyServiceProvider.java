package is.yarr.qilletni.music.spotify.provider;

import is.yarr.qilletni.api.auth.ServiceProvider;
import is.yarr.qilletni.api.music.ConsolePlayActor;
import is.yarr.qilletni.api.music.MusicCache;
import is.yarr.qilletni.api.music.MusicFetcher;
import is.yarr.qilletni.api.music.TrackOrchestrator;
import is.yarr.qilletni.music.spotify.SpotifyMusicCache;
import is.yarr.qilletni.music.spotify.SpotifyMusicFetcher;
import is.yarr.qilletni.music.spotify.SpotifyTrackOrchestrator;
import is.yarr.qilletni.music.spotify.auth.SpotifyAuthorizer;
import is.yarr.qilletni.music.spotify.auth.pkce.SpotifyPKCEAuthorizer;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SpotifyServiceProvider implements ServiceProvider {
    
    private MusicCache musicCache;
    private MusicFetcher musicFetcher;
    private TrackOrchestrator trackOrchestrator;
    
    @Override
    public CompletableFuture<Void> initialize() {
        SpotifyAuthorizer authorizer = SpotifyPKCEAuthorizer.createWithCodes();
        
        return authorizer.authorizeSpotify().thenRun(() -> {
            var spotifyMusicFetcher = new SpotifyMusicFetcher(authorizer);
            musicFetcher = spotifyMusicFetcher;
            musicCache = new SpotifyMusicCache(spotifyMusicFetcher);
            trackOrchestrator = new SpotifyTrackOrchestrator(new ConsolePlayActor(), musicCache);
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
}

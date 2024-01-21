package is.yarr.qilletni.music.spotify.provider;

import is.yarr.qilletni.api.auth.ServiceProvider;
import is.yarr.qilletni.api.music.MusicCache;
import is.yarr.qilletni.api.music.MusicFetcher;
import is.yarr.qilletni.music.spotify.SpotifyMusicCache;
import is.yarr.qilletni.music.spotify.SpotifyMusicFetcher;
import is.yarr.qilletni.music.spotify.auth.SpotifyAuthorizer;
import is.yarr.qilletni.music.spotify.auth.pkce.SpotifyPKCEAuthorizer;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SpotifyServiceProvider implements ServiceProvider {
    
    private MusicCache musicCache;
    private MusicFetcher musicFetcher;
    
    @Override
    public CompletableFuture<MusicCache> initialize() {
        SpotifyAuthorizer authorizer = SpotifyPKCEAuthorizer.createWithCodes();
        
        return authorizer.authorizeSpotify().thenApply($ -> {
            var spotifyMusicFetcher = new SpotifyMusicFetcher(authorizer);
            musicFetcher = spotifyMusicFetcher;
            return musicCache = new SpotifyMusicCache(spotifyMusicFetcher);
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
}

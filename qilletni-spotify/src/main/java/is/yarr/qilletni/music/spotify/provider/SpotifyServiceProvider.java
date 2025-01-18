package is.yarr.qilletni.music.spotify.provider;

import is.yarr.qilletni.api.auth.ServiceProvider;
import is.yarr.qilletni.api.exceptions.config.ConfigInitializeException;
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
import is.yarr.qilletni.async.ExecutorServiceUtility;
import is.yarr.qilletni.database.HibernateUtil;
import is.yarr.qilletni.music.spotify.SpotifyMusicCache;
import is.yarr.qilletni.music.spotify.SpotifyMusicFetcher;
import is.yarr.qilletni.music.spotify.SpotifyMusicTypeConverter;
import is.yarr.qilletni.music.spotify.SpotifyStringIdentifier;
import is.yarr.qilletni.music.spotify.auth.SpotifyApiSingleton;
import is.yarr.qilletni.music.spotify.auth.SpotifyAuthorizer;
import is.yarr.qilletni.music.spotify.auth.pkce.SpotifyPKCEAuthorizer;
import is.yarr.qilletni.music.spotify.creator.PlaylistCreator;
import is.yarr.qilletni.music.spotify.play.ReroutablePlayActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class SpotifyServiceProvider implements ServiceProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyServiceProvider.class);
    
    private PackageConfig packageConfig;
    private SpotifyMusicCache musicCache;
    private MusicFetcher musicFetcher;
    private TrackOrchestrator trackOrchestrator;
    private MusicTypeConverter musicTypeConverter;
    private StringIdentifier stringIdentifier;
    private SpotifyAuthorizer authorizer;
    
    private static ServiceProvider serviceProviderInstance;
    
    @Override
    public CompletableFuture<Void> initialize(BiFunction<PlayActor, MusicCache, TrackOrchestrator> defaultTrackOrchestratorFunction, PackageConfig packageConfig) {
        this.packageConfig = packageConfig;
        initConfig();
        
        authorizer = SpotifyPKCEAuthorizer.createWithCodes(packageConfig, packageConfig.getOrThrow("clientId"), packageConfig.getOrThrow("redirectUri"));
        
        return authorizer.authorizeSpotify().thenRun(() -> {
            var spotifyMusicFetcher = new SpotifyMusicFetcher(authorizer);
            SpotifyApiSingleton.setSpotifyAuthorizer(authorizer);
            musicFetcher = spotifyMusicFetcher;
            musicCache = new SpotifyMusicCache(spotifyMusicFetcher);
            trackOrchestrator = defaultTrackOrchestratorFunction.apply(new ReroutablePlayActor(), musicCache);

            musicTypeConverter = new SpotifyMusicTypeConverter(musicCache);
            
            serviceProviderInstance = this;
        });
    }

    @Override
    public void shutdown() {
        authorizer.shutdown();
        ExecutorServiceUtility.shutdown(PlaylistCreator.EXECUTOR_SERVICE);
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
    public MusicTypeConverter getMusicTypeConverter() {
        return Objects.requireNonNull(musicTypeConverter, "ServiceProvider#initialize must be invoked to initialize MusicTypeConverter");
    }

    @Override
    public StringIdentifier getStringIdentifier(SongTypeFactory songTypeFactory, CollectionTypeFactory collectionTypeFactory, AlbumTypeFactory albumTypeFactory) {
        if (stringIdentifier == null) {
            return stringIdentifier = new SpotifyStringIdentifier(musicCache, songTypeFactory, collectionTypeFactory, albumTypeFactory);
        }
        
        return stringIdentifier;
    }
    
    private void initConfig() {
        packageConfig.loadConfig();
        
        var requiredOptions = List.of("clientId", "redirectUri", "dbUrl", "dbUsername", "dbPassword");
        var allFound = true;

        for (var option : requiredOptions) {
            if (packageConfig.get(option).isEmpty()) {
                allFound = false;
                LOGGER.error("Required config value '{}' not found in Spotify config", option);
            }
        }
        
        if (!allFound) {
            throw new ConfigInitializeException("Spotify config is missing required options, aborting");
        }

        HibernateUtil.initializeSessionFactory(packageConfig.getOrThrow("dbUrl"), packageConfig.getOrThrow("dbUsername"), packageConfig.getOrThrow("dbPassword"));
    }
    
    public static ServiceProvider getServiceProviderInstance() {
        Objects.requireNonNull(serviceProviderInstance, "ServiceProvider#initialize must be invoked to initialize ServiceProvider");
        return serviceProviderInstance;
    }
}

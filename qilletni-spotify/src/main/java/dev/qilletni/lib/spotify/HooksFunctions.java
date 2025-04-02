package dev.qilletni.lib.spotify;

import dev.qilletni.api.lang.internal.BackgroundTaskExecutor;
import dev.qilletni.api.lang.internal.FunctionInvoker;
import dev.qilletni.api.lang.types.FunctionType;
import dev.qilletni.api.music.MusicCache;
import dev.qilletni.api.music.Track;
import dev.qilletni.api.music.factories.SongTypeFactory;
import dev.qilletni.api.music.supplier.DynamicProvider;
import dev.qilletni.lib.spotify.music.auth.SpotifyApiSingleton;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class HooksFunctions {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HooksFunctions.class);

    private final SpotifyApi spotifyApi;
    private final BackgroundTaskExecutor backgroundTaskExecutor;
    private final FunctionInvoker functionInvoker;
    private final SongTypeFactory songTypeConverter;
    private final MusicCache musicCache;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    private final AtomicReference<String> previouslyPlayingId = new AtomicReference<>("");

    public HooksFunctions(BackgroundTaskExecutor backgroundTaskExecutor, FunctionInvoker functionInvoker, SongTypeFactory songTypeConverter, DynamicProvider dynamicProvider) {
        this.backgroundTaskExecutor = backgroundTaskExecutor;
        this.functionInvoker = functionInvoker;
        this.songTypeConverter = songTypeConverter;
        this.musicCache = dynamicProvider.getMusicCache();
        this.spotifyApi = SpotifyApiSingleton.getSpotifyApi();
    }
    
    public int onSongPlay(FunctionType callback, int pollTime) {
        final int conditionId = backgroundTaskExecutor.runWhenCondition((Track track) -> {
            // Convert the track to a song on the program thread and invoke the callback
            functionInvoker.invokeFunction(callback, List.of(songTypeConverter.createSongFromTrack(track)));
        });
        
        LOGGER.debug("Polling Spotify every {}ms for song changes, conditionId = {}", pollTime, conditionId);
        
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                var currentlyPlaying = this.spotifyApi.getUsersCurrentlyPlayingTrack().build().execute();
                
                var currentlyPlayingId = currentlyPlaying.getItem().getId();
                var cachedPreviouslyPlayingId = previouslyPlayingId.getAndSet(currentlyPlayingId);
                
                if (!cachedPreviouslyPlayingId.equals(currentlyPlayingId)) { // Different than last checked
                    LOGGER.debug("New song!");
                    
                    musicCache.getTrackById(currentlyPlayingId).ifPresent(track -> {
                        LOGGER.debug("New song track: {}", track);
                        
                        backgroundTaskExecutor.triggerCondition(conditionId, track);
                    });
                } else {
                    LOGGER.debug("songs:   {} == {}", cachedPreviouslyPlayingId, currentlyPlayingId);
                }
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                LOGGER.error("Error polling Spotify", e);
            }
        }, pollTime, pollTime, TimeUnit.MILLISECONDS);
        
        return conditionId;
    }
}

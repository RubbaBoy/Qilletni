package dev.qilletni.lib.spotify.music;

import dev.qilletni.api.music.Track;
import dev.qilletni.api.music.play.PlayActor;
import dev.qilletni.lib.spotify.music.auth.SpotifyApiSingleton;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.IPlaylistItem;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class QueuePlayActor implements PlayActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueuePlayActor.class);

    private static boolean blocking = false;
    private static boolean fastPolling = false;
    private final static int initialSongBufferSize = 2;
    private int songsAdded = 0;
    
    private static final int DEFAULT_POLL_TIME = 10000; // 10 sec
    private static final int MIN_POLL_TIME = 5000;   // 5 sec
    private static final int MAX_POLL_TIME = 300000; // 5 min 

    private final SpotifyApi spotifyApi = SpotifyApiSingleton.getSpotifyApi();

    public static void setBlocking(boolean blocking) {
        QueuePlayActor.blocking = blocking;
    }

    public static void setFastPolling(boolean fastPolling) {
        QueuePlayActor.fastPolling = fastPolling;
    }

    @Override
    public CompletableFuture<PlayResult> playTrack(Track track) {
        LOGGER.debug("Adding track {} to queue", track.getId());
        try {
            if (!blocking) {
                LOGGER.debug("Not blocking, adding track {} to queue immediately", track.getId());
                
                spotifyApi.addItemToUsersPlaybackQueue("spotify:track:%s".formatted(track.getId())).build().execute();
                return CompletableFuture.completedFuture(PlayResult.SUCCESS);
            }

            // Is blocking

            if (songsAdded < initialSongBufferSize) {
                LOGGER.debug("Adding track {} to queue immediately to satisfy buffer", track.getId());
                
                spotifyApi.addItemToUsersPlaybackQueue("spotify:track:%s".formatted(track.getId())).build().execute();
                songsAdded++;
                return CompletableFuture.completedFuture(PlayResult.SUCCESS);
            }

            waitForSongToChange();
            
            spotifyApi.addItemToUsersPlaybackQueue("spotify:track:%s".formatted(track.getId())).build().execute();
            
            return CompletableFuture.completedFuture(PlayResult.SUCCESS);
        } catch (IOException | SpotifyWebApiException | ParseException | InterruptedException e) {
            LOGGER.error("Unable to add track to queue", e);
            return CompletableFuture.completedFuture(PlayResult.ERROR);
        }
    }

    /**
     * Waits for the user's queue to reach the given size or less.
     * 
     * @param size The maximum size to wait for the queue to reach
     */
    private void waitForQueueSize(int size) throws IOException, ParseException, InterruptedException, SpotifyWebApiException {
        if (size < 0) {
            LOGGER.debug("Invalid size of {}, continuing without waiting", size);
            return;
        }

        // Loop in case the song changes but the user adds more songs to the queue manually (or other similar cases)
        int currentQueueSize;
        while ((currentQueueSize = getQueueSize()) > size) {
            LOGGER.debug("Queue size is {}, waiting for it to be {}", currentQueueSize, size);
            waitForSongToChange();
        }
        
        LOGGER.debug("Queue size is now {}", getQueueSize());
    }
    
    private CurrentlyPlaying getCurrentlyPlaying() throws IOException, ParseException, SpotifyWebApiException {
        return spotifyApi.getUsersCurrentlyPlayingTrack().build().execute();
    }
    
    private int getQueueSize() throws IOException, ParseException, SpotifyWebApiException {
        var queue = spotifyApi.getTheUsersQueue().build().execute().getQueue();
        System.out.println(queue.stream().map(IPlaylistItem::getName).collect(Collectors.joining(", ")));
        return queue.size();
    }

    /**
     * Pauses the thread and waits for the current song to change (likely finishing).
     */
    private void waitForSongToChange() throws IOException, ParseException, SpotifyWebApiException, InterruptedException {
        var previouslyPlaying = getCurrentlyPlaying();
        String currentPlayingId;
        int timeLeftMs = previouslyPlaying.getItem().getDurationMs() - previouslyPlaying.getProgress_ms();
    
        do {
            var sleepTime = DEFAULT_POLL_TIME;
            
            if (!fastPolling) {
                sleepTime = (int) (timeLeftMs * 0.75);
                LOGGER.debug("Not fast polling, sleep time: {}ms", sleepTime);
            }
            
            sleepTime = Math.clamp(sleepTime, MIN_POLL_TIME, MAX_POLL_TIME);
            
            LOGGER.debug("Sleeping for {}ms", sleepTime);
            Thread.sleep(sleepTime);
            
            LOGGER.debug("Checking currently playing song");
            var currentPlaying = getCurrentlyPlaying();
            currentPlayingId = currentPlaying.getItem().getId();
            timeLeftMs = currentPlaying.getItem().getDurationMs() - currentPlaying.getProgress_ms();
            
        } while (previouslyPlaying.getItem().getId().equals(currentPlayingId));
        
        LOGGER.debug("Song changed!");
    }
}

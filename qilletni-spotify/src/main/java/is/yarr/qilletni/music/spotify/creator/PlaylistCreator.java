package is.yarr.qilletni.music.spotify.creator;

import com.google.gson.JsonArray;
import is.yarr.qilletni.api.music.Playlist;
import is.yarr.qilletni.api.music.Track;
import is.yarr.qilletni.music.spotify.SpotifyMusicFetcher;
import is.yarr.qilletni.music.spotify.auth.SpotifyAuthorizer;
import is.yarr.qilletni.music.spotify.entities.SpotifyPlaylist;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PlaylistCreator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaylistCreator.class);

    private final SpotifyAuthorizer authorizer;

    public PlaylistCreator(SpotifyAuthorizer authorizer) {
        this.authorizer = authorizer;
    }
    
    public CompletableFuture<SpotifyPlaylist> createPlaylist(String name) {
        var spotifyApi = authorizer.getSpotifyApi();
        var currentUser = authorizer.getCurrentUser().orElseThrow(() -> new RuntimeException("No current user authenticated!"));
        
        return spotifyApi.createPlaylist(currentUser.getId(), name)
                .build().executeAsync()
                .thenApply(SpotifyMusicFetcher::createPlaylistEntity);
    }
    
    public CompletableFuture<SpotifyPlaylist> createPlaylist(String name, String description) {
        var spotifyApi = authorizer.getSpotifyApi();
        var currentUser = authorizer.getCurrentUser().orElseThrow(() -> new RuntimeException("No current user authenticated!"));
        
        return spotifyApi.createPlaylist(currentUser.getId(), name)
                .description(description).build().executeAsync()
                .thenApply(SpotifyMusicFetcher::createPlaylistEntity);
    }
    
    public CompletableFuture<Void> addToPlaylist(Playlist playlist, List<Track> tracks) {
        LOGGER.debug("Adding {} tracks to playlist '{}'", tracks.size(), playlist.getTitle());
        var spotifyApi = authorizer.getSpotifyApi();

        return CompletableFuture.runAsync(() -> {
            try {
                for (int i = 0; i < tracks.size(); i += 100) {
                    var addingTracks = tracks.subList(i, Math.min(tracks.size(), i + 100));

                    var trackJsonArray = new JsonArray();
                    addingTracks.stream().map(track -> "spotify:track:" + track.getId()).forEach(trackJsonArray::add);
                    spotifyApi.addItemsToPlaylist(playlist.getId(), trackJsonArray).build().execute();
                }
            } catch (IOException | ParseException | SpotifyWebApiException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
}

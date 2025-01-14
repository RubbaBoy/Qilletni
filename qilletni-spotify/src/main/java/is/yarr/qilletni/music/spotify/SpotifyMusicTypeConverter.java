package is.yarr.qilletni.music.spotify;

import is.yarr.qilletni.api.music.Album;
import is.yarr.qilletni.api.music.Artist;
import is.yarr.qilletni.api.music.MusicTypeConverter;
import is.yarr.qilletni.api.music.Playlist;
import is.yarr.qilletni.api.music.Track;
import is.yarr.qilletni.api.music.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Converts a type from another service provider to Spotify.
 */
public class SpotifyMusicTypeConverter implements MusicTypeConverter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyMusicTypeConverter.class);

    private final SpotifyMusicCache musicCache;

    public SpotifyMusicTypeConverter(SpotifyMusicCache musicCache) {
        this.musicCache = musicCache;
    }
    
    // TODO: Add biases and/or other logic to determine the best track to convert
    //       This may include mapping an ID or specified name to a Spotify track/ID

    @Override
    public Optional<Track> convertTrack(List<Track> tracks) {
        // Try all tracks
        for (var track : tracks) {
            var artistName = track.getArtist().getName();
            if (artistName == null) {
                continue; // Just in case, for some reason this is null
            }

            var trackOptional = musicCache.getTrack(track.getName(), artistName);
            if (trackOptional.isPresent()) {
                return trackOptional;
            }
        }
        
        return Optional.empty();
    }

    @Override
    public Optional<Album> convertAlbum(List<Album> albums) {
        // Try all albums
        for (var album : albums) {
            var artistName = album.getArtist().getName();
            if (artistName == null) {
                continue; // Just in case, for some reason this is null
            }

            var albumOptional = musicCache.getAlbum(album.getName(), artistName);
            if (albumOptional.isPresent()) {
                return albumOptional;
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Artist> convertArtist(List<Artist> artists) {
        // Try all artists
        for (var artist : artists) {
            var artistOptional = musicCache.getArtistByName(artist.getName());
            if (artistOptional.isPresent()) {
                return artistOptional;
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Playlist> convertPlaylist(List<Playlist> playlists) {
        // We don't support playlist conversion in this package
        LOGGER.warn("Playlist conversion is not supported in the Spotify service provider");
        return Optional.empty();
    }

    @Override
    public Optional<User> convertUser(List<User> users) {
        // We don't support playlist conversion in this package
        LOGGER.warn("User conversion is not supported in the Spotify service provider");
        return Optional.empty();
    }
}

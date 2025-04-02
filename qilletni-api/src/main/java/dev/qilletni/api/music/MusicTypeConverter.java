package dev.qilletni.api.music;

import java.util.List;
import java.util.Optional;

/**
 * Converts music types from one service provider to another. Often times, a conversion is not possible. Examples of
 * this include a playlist that is only available on Spotify, or an album that is only available on Last.fm. The most
 * important example of this is typically converting a track from one service provider to another, to allow, for
 * instance, playing tracks from Last.Fm on Spotify.
 * <br><br>
 * Multiple types may be given to each method, one for every service provider that the type is available on currently.
 * There will always be at least one in the list, so {@link List#getFirst()} may be used.
 */
public interface MusicTypeConverter {

    /**
     * Converts a track from one service provider to another.
     * 
     * @param tracks The tracks to convert
     * @return The created track in the current service provider's implementation
     */
    Optional<Track> convertTrack(List<Track> tracks);

    /**
     * Converts an album from one service provider to another.
     * 
     * @param albums The albums to convert
     * @return The created album in the current service provider's implementation
     */
    Optional<Album> convertAlbum(List<Album> albums);

    /**
     * Converts an artist from one service provider to another.
     * 
     * @param artists The artists to convert
     * @return The created artist in the current service provider's implementation
     */
    Optional<Artist> convertArtist(List<Artist> artists);

    /**
     * Converts a playlist from one service provider to another.
     * 
     * @param playlists The playlists to convert
     * @return The created playlist in the current service provider's implementation
     */
    Optional<Playlist> convertPlaylist(List<Playlist> playlists);

    /**
     * Converts a user from one service provider to another.
     * 
     * @param users The users converting from
     * @return The created user in the current service provider's implementation
     */
    Optional<User> convertUser(List<User> users);
    
}

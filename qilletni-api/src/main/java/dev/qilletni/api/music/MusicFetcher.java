package dev.qilletni.api.music;

import java.util.List;
import java.util.Optional;

public interface MusicFetcher {

    /**
     * Gets a {@link Track} from a given name and artist. If multiple tracks are found with the name/artist combination,
     * it is up to the implementation to decide what to pick.
     *
     * @param name The name of the track, not including the artist name
     * @param artist The name of the artist
     * @return The track with the given name and artist, if any
     */
    Optional<Track> fetchTrack(String name, String artist);

    /**
     * Gets a {@link Track} from a given ID, specific to the service provider implementation.
     *
     * @param id The ID of the track. This is in different formats for each service provider implementation.
     * @return The track with the given ID, if any
     */
    Optional<Track> fetchTrackById(String id);

    /**
     * Gets multiple {@link Track}s from a given list of {@link MusicFetcher.TrackNameArtist}s. This effectively
     * performs what {@link #fetchTrack(String, String)} does, but batched for multiple tracks at once.
     *
     * @param tracks The tracks to look up
     * @return The list of tracks that were found, in the same order as the given list of tracks. If a track isn't
     * found, it is skipped
     */
    List<Track> fetchTracks(List<TrackNameArtist> tracks);

    /**
     * Gets multiple {@link Track}s from a given list of IDs. This effectively performs what {@link #fetchTrackById(String)}
     * does, but batched for multiple tracks at once. The IDs are in different formats for each service provider.
     *
     * @param trackIds The IDs of the tracks to look up
     * @return The list of tracks that were found, in the same order as the given list of track IDs. If a track isn't
     * found, it is skipped.
     */
    List<Track> fetchTracksById(List<String> trackIds);

    /**
     * Gets a {@link Playlist} from a given name and author. If multiple playlists are found with the name/author
     * combination, it is up to the implementation to decide what to pick. If multiple playlists are found with the
     * same name, it is up to the implementation to decide what to pick.
     *
     * @param name The name of the playlist
     * @param author The name of the artist
     * @return The playlist with the given name and author, if any
     */
    Optional<Playlist> fetchPlaylist(String name, String author);

    /**
     * Gets a {@link Playlist} from a given ID. This is in different formats for each service provider implementation.
     *
     * @param id The ID of the playlist. This is in different formats for each service provider implementation
     * @return The playlist with the given ID, if any
     */
    Optional<Playlist> fetchPlaylistById(String id);

    /**
     * Gets an {@link Album} from a given name and artist. If multiple albums are found with the name/artist combination,
     * it is up to the implementation to decide what to pick. If multiple albums are found with the same name, it is
     * up to the implementation to decide what to pick.
     *
     * @param name The name of the album, not including the artist name
     * @param artist The name of the artist
     * @return The album with the given name and artist, if any
     */
    Optional<Album> fetchAlbum(String name, String artist);

    /**
     * Gets an {@link Album} from a given ID. This is in different formats for each service provider implementation.
     *
     * @param id The ID of the album. This is in different formats for each service provider implementation
     * @return The album with the given ID, if any
     */
    Optional<Album> fetchAlbumById(String id);

    /**
     * Gets all tracks from a given {@link Album}.
     *
     * @param album The album to get tracks from
     * @return A list of all tracks in the album, in the order they appear in the album.
     */
    List<Track> fetchAlbumTracks(Album album);

    /**
     * Gets all tracks from a given {@link Playlist}.
     *
     * @param playlist The playlist to get tracks from
     * @return A list of all tracks in the playlist, in the order they appear in the playlist
     */
    List<Track> fetchPlaylistTracks(Playlist playlist);

    /**
     * Gets an {@link Artist} from a given ID. This is in different formats for each service provider implementation.
     *
     * @param id The ID of the artist. This is in different formats for each service provider implementation.
     * @return The artist with the given ID, if any
     */
    Optional<Artist> fetchArtistById(String id);

    /**
     * Gets an {@link Artist} from a given name. If multiple artists are found with the same name, it is up to the
     * implementation to decide what to pick.
     *
     * @param name The full name of the artist to look up
     * @return The artist with the given name, if any
     */
    Optional<Artist> fetchArtistByName(String name);

    /**
     * An object to hold a name and artist combination.
     * 
     * @param name A name
     * @param artist An artist's name
     */
    record TrackNameArtist(String name, String artist) {

        /**
         * Checks if the given track matches this track name and artist combination.
         * 
         * @param track The track to check
         * @return If the track has the current name and artist combination
         */
        public boolean matchesTrack(Track track) {
            return track.getName().equals(name) && track.getArtist().getName().equals(artist);
        }
    }
    
}

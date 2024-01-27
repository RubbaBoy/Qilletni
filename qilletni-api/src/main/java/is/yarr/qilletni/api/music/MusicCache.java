package is.yarr.qilletni.api.music;

import java.util.List;
import java.util.Optional;

public interface MusicCache {

    Optional<Track> getTrack(String name, String artist);

    Optional<Track> getTrackById(String id);

    List<Track> getTracks(List<MusicFetcher.TrackNameArtist> tracks);

    List<Track> getTracksById(List<String> trackIds);

    Optional<Playlist> getPlaylist(String name, String author);

    Optional<Playlist> getPlaylistById(String id);

    Optional<Album> getAlbum(String name, String artist);

    Optional<Album> getAlbumById(String id);

    List<Track> getAlbumTracks(Album album);

    List<Track> getPlaylistTracks(Playlist playlist);
    
    Optional<Artist> getArtistById(String id);
    
    Optional<Artist> getArtistByName(String name);

    /**
     * Takes in a URL or an ID and returns the ID.
     *
     * @param idOrUrl The URL or ID
     * @return The ID
     */
    String getIdFromString(String idOrUrl);
    
}

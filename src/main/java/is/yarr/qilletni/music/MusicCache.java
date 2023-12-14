package is.yarr.qilletni.music;

import java.util.List;
import java.util.Optional;

public interface MusicCache {

    Optional<Track> getTrack(String name, String artist);

    Optional<Track> getTrackById(String id);

    Optional<List<Track>> getTracks(List<MusicSupplier.TrackNameArtist> tracks);

    Optional<List<Track>> getTracksById(List<String> trackIds);

    Optional<Playlist> getPlaylist(String name, String author);

    Optional<Playlist> getPlaylistById(String id);

    Optional<Album> getAlbum(String name, String artist);

    Optional<List<Track>> getAlbumTracks(Album album);

    Optional<List<Track>> getPlaylistTracks(Playlist playlist);
    
}

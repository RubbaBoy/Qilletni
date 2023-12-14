package is.yarr.qilletni.music;

import java.util.List;
import java.util.Optional;

public interface MusicSupplier {
    
    Optional<Track> fetchTrack(String name, String artist);
    
    Optional<Track> fetchTrackById(String id);
    
    Optional<List<Track>> fetchTracks(List<TrackNameArtist> tracks);
    
    Optional<List<Track>> fetchTracksById(List<String> trackIds);
    
    Optional<Playlist> fetchPlaylist(String name, String author);
    
    Optional<Playlist> fetchPlaylistById(String id);
    
    Optional<Album> fetchAlbum(String name, String artist);
    
    Optional<List<Track>> fetchAlbumTracks(Album album);
    
    Optional<List<Track>> fetchPlaylistTracks(Playlist playlist);
    
    record TrackNameArtist(String name, String artist) {}
    
}

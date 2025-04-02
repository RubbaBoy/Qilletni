package dev.qilletni.api.music;

import java.util.List;
import java.util.Optional;

public interface MusicFetcher {
    
    Optional<Track> fetchTrack(String name, String artist);
    
    Optional<Track> fetchTrackById(String id);
    
    List<Track> fetchTracks(List<TrackNameArtist> tracks);
    
    List<Track> fetchTracksById(List<String> trackIds);
    
    Optional<Playlist> fetchPlaylist(String name, String author);
    
    Optional<Playlist> fetchPlaylistById(String id);
    
    Optional<Album> fetchAlbum(String name, String artist);
    
    Optional<Album> fetchAlbumById(String id);
    
    List<Track> fetchAlbumTracks(Album album);
    
    List<Track> fetchPlaylistTracks(Playlist playlist);
    
    Optional<Artist> fetchArtistByName(String name);
    
    Optional<Artist> fetchArtistById(String id);
    
    record TrackNameArtist(String name, String artist) {
        public boolean matchesTrack(Track track) {
            return track.getName().equals(name) && track.getArtist().getName().equals(artist);
        }
    }
    
}

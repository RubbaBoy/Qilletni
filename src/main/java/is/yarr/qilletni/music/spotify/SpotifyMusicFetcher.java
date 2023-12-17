package is.yarr.qilletni.music.spotify;

import is.yarr.qilletni.music.Album;
import is.yarr.qilletni.music.Artist;
import is.yarr.qilletni.music.MusicFetcher;
import is.yarr.qilletni.music.Playlist;
import is.yarr.qilletni.music.Track;

import java.util.List;
import java.util.Optional;

public class SpotifyMusicFetcher implements MusicFetcher {
    
    @Override
    public Optional<Track> fetchTrack(String name, String artist) {
        return Optional.empty();
    }

    @Override
    public Optional<Track> fetchTrackById(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Track>> fetchTracks(List<TrackNameArtist> tracks) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Track>> fetchTracksById(List<String> trackIds) {
        return Optional.empty();
    }

    @Override
    public Optional<Playlist> fetchPlaylist(String name, String author) {
        return Optional.empty();
    }

    @Override
    public Optional<Playlist> fetchPlaylistById(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<Album> fetchAlbum(String name, String artist) {
        return Optional.empty();
    }

    @Override
    public Optional<Album> fetchAlbumById(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Track>> fetchAlbumTracks(Album album) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Track>> fetchPlaylistTracks(Playlist playlist) {
        return Optional.empty();
    }

    @Override
    public Optional<Artist> fetchArtistByName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Artist> fetchArtistById(String id) {
        return Optional.empty();
    }
}

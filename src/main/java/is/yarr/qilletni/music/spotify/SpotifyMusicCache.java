package is.yarr.qilletni.music.spotify;

import is.yarr.qilletni.database.EntityTransaction;
import is.yarr.qilletni.music.Album;
import is.yarr.qilletni.music.Artist;
import is.yarr.qilletni.music.MusicCache;
import is.yarr.qilletni.music.MusicFetcher;
import is.yarr.qilletni.music.Playlist;
import is.yarr.qilletni.music.Track;
import is.yarr.qilletni.music.spotify.entities.SpotifyAlbum;
import is.yarr.qilletni.music.spotify.entities.SpotifyArtist;
import is.yarr.qilletni.music.spotify.entities.SpotifyTrack;

import javax.persistence.criteria.Join;
import java.util.List;
import java.util.Optional;

public class SpotifyMusicCache implements MusicCache {
    
    private final SpotifyMusicFetcher spotifyMusicFetcher;

    public SpotifyMusicCache(SpotifyMusicFetcher spotifyMusicFetcher) {
        this.spotifyMusicFetcher = spotifyMusicFetcher;
    }

    @Override
    public Optional<Track> getTrack(String name, String artist) {
        try (var entityTransaction = EntityTransaction.beginTransaction()) {
            var session = entityTransaction.getSession();
            
            var builder = session.getCriteriaBuilder();
            var criteria = builder.createQuery(SpotifyTrack.class);
            var root = criteria.from(SpotifyTrack.class);

            Join<SpotifyTrack, SpotifyArtist> departmentJoin = root.join("artists");

            var trackNamePredicate = builder.equal(root.get("name"), name);
            var artistPredicate = builder.equal(departmentJoin.get("name"), artist);

            criteria.where(trackNamePredicate, artistPredicate);

            var tracks = session.createQuery(criteria).getResultList();

            if (!tracks.isEmpty()) {
                return Optional.of(tracks.get(0));
            }
        }
        
        return spotifyMusicFetcher.fetchTrack(name, artist);
    }

    @Override
    public Optional<Track> getTrackById(String id) {
        return spotifyMusicFetcher.fetchTrackById(id);
    }

    @Override
    public Optional<List<Track>> getTracks(List<MusicFetcher.TrackNameArtist> tracks) {
        return spotifyMusicFetcher.fetchTracks(tracks);
    }

    @Override
    public Optional<List<Track>> getTracksById(List<String> trackIds) {
        return spotifyMusicFetcher.fetchTracksById(trackIds);
    }

    @Override
    public Optional<Playlist> getPlaylist(String name, String author) {
        return spotifyMusicFetcher.fetchPlaylist(name, author);
    }

    @Override
    public Optional<Playlist> getPlaylistById(String id) {
        return spotifyMusicFetcher.fetchPlaylistById(id);
    }

    @Override
    public Optional<Album> getAlbum(String name, String artist) {
        try (var entityTransaction = EntityTransaction.beginTransaction()) {
            var session = entityTransaction.getSession();

            var builder = session.getCriteriaBuilder();
            var criteria = builder.createQuery(SpotifyAlbum.class);
            var root = criteria.from(SpotifyAlbum.class);

            Join<SpotifyAlbum, SpotifyArtist> departmentJoin = root.join("artists");

            var albumNamePredicate = builder.equal(root.get("name"), name);
            var artistPredicate = builder.equal(departmentJoin.get("name"), artist);

            criteria.where(albumNamePredicate, artistPredicate);

            var albums = session.createQuery(criteria).getResultList();

            if (!albums.isEmpty()) {
                return Optional.of(albums.get(0));
            }
        }

        return spotifyMusicFetcher.fetchAlbum(name, artist);
    }

    @Override
    public Optional<Album> getAlbumById(String id) {
        return spotifyMusicFetcher.fetchAlbumById(id);
    }

    @Override
    public Optional<List<Track>> getAlbumTracks(Album album) {
        return spotifyMusicFetcher.fetchAlbumTracks(album);
    }

    @Override
    public Optional<List<Track>> getPlaylistTracks(Playlist playlist) {
        return spotifyMusicFetcher.fetchPlaylistTracks(playlist);
    }

    @Override
    public Optional<Artist> getArtistById(String id) {
        return spotifyMusicFetcher.fetchArtistById(id);
    }

    @Override
    public Optional<Artist> getArtistByName(String name) {
        return spotifyMusicFetcher.fetchArtistByName(name);
    }
}

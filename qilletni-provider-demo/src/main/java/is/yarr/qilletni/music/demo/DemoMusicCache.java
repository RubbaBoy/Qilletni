package is.yarr.qilletni.music.demo;

import is.yarr.qilletni.api.exceptions.InvalidURLOrIDException;
import is.yarr.qilletni.api.music.Album;
import is.yarr.qilletni.api.music.Artist;
import is.yarr.qilletni.api.music.MusicCache;
import is.yarr.qilletni.api.music.MusicFetcher;
import is.yarr.qilletni.api.music.Playlist;
import is.yarr.qilletni.api.music.Track;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class DemoMusicCache implements MusicCache {
    
    private final DemoMusicFetcher demoMusicFetcher;

    public DemoMusicCache(DemoMusicFetcher demoMusicFetcher) {
        this.demoMusicFetcher = demoMusicFetcher;
    }

    @Override
    public Optional<Track> getTrack(String name, String artist) {
        // Would test against database
        
        return demoMusicFetcher.fetchTrack(name, artist);
    }

    @Override
    public Optional<Track> getTrackById(String id) {
        // Would test against database

        return demoMusicFetcher.fetchTrackById(id);
    }

    @Override
    public List<Track> getTracks(List<MusicFetcher.TrackNameArtist> tracks) {
        // Would test against database

        return demoMusicFetcher.fetchTracks(tracks);
    }

    @Override
    public List<Track> getTracksById(List<String> trackIds) {
        // Would test against database

        return demoMusicFetcher.fetchTracksById(trackIds);
    }

    @Override
    public Optional<Playlist> getPlaylist(String name, String author) {
        // Would test against database

        return demoMusicFetcher.fetchPlaylist(name, author);
    }

    @Override
    public Optional<Playlist> getPlaylistById(String id) {
        // Would test against database

        return demoMusicFetcher.fetchPlaylistById(id);
    }

    @Override
    public Optional<Album> getAlbum(String name, String artist) {
        // Would test against database

        return demoMusicFetcher.fetchAlbum(name, artist);
    }

    @Override
    public Optional<Album> getAlbumById(String id) {
        // Would test against database

        return demoMusicFetcher.fetchAlbumById(id);
    }

    @Override
    public List<Track> getAlbumTracks(Album album) {
        // Would test against database

        return demoMusicFetcher.fetchAlbumTracks(album);
    }

    @Override
    public List<Track> getPlaylistTracks(Playlist playlist) {
        // Would test against database

        return demoMusicFetcher.fetchPlaylistTracks(playlist);
    }

    @Override
    public Optional<Artist> getArtistById(String id) {
        // Would test against database

        return demoMusicFetcher.fetchArtistById(id);
    }

    @Override
    public Optional<Artist> getArtistByName(String name) {
        // Would test against database

        return demoMusicFetcher.fetchArtistByName(name);
    }

    /**
     * This parses IDs of 6 characters, or example.com/{id}
     * 
     * @param idOrUrl The URL or ID
     * @return The resulting ID, if found
     */
    @Override
    public String getIdFromString(String idOrUrl) {
        // Regular expression to match Spotify track URLs or an ID
        var pattern = Pattern.compile("example\\.com/track/(\\w{6})|^(\\w{6})$");
        var matcher = pattern.matcher(idOrUrl);

        if (matcher.find()) {
            // Check which group has a match
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    return matcher.group(i);
                }
            }
        }

        throw new InvalidURLOrIDException(String.format("Invalid URL or ID: \"%s\"", idOrUrl));
    }
}

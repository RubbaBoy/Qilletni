package is.yarr.qilletni.music.spotify;

import is.yarr.qilletni.music.Album;
import is.yarr.qilletni.music.Artist;
import is.yarr.qilletni.music.MusicFetcher;
import is.yarr.qilletni.music.Playlist;
import is.yarr.qilletni.music.Track;
import is.yarr.qilletni.music.spotify.entities.SpotifyAlbum;
import is.yarr.qilletni.music.spotify.entities.SpotifyArtist;
import is.yarr.qilletni.music.spotify.entities.SpotifyTrack;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SpotifyMusicFetcher implements MusicFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyMusicFetcher.class);

    private static final int MAX_PAGE_LIMIT = 50;

    private final SpotifyAuthorizer authorizer;

    public SpotifyMusicFetcher(SpotifyAuthorizer authorizer) {
        this.authorizer = authorizer;
    }

    @Override
    public Optional<Track> fetchTrack(String name, String artist) {
        return Optional.empty();
    }

    @Override
    public Optional<Track> fetchTrackById(String id) {
        return Optional.empty();
    }

    @Override
    public List<Track> fetchTracks(List<TrackNameArtist> tracks) {
        return Collections.emptyList();
    }

    @Override
    public List<Track> fetchTracksById(List<String> trackIds) {
        return Collections.emptyList();
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
    public List<Track> fetchAlbumTracks(Album album) {
        return Collections.emptyList();
    }

    @Override
    public List<Track> fetchPlaylistTracks(Playlist playlist) {
        var spotifyApi = authorizer.getSpotifyApi();

        try {
            var tracks = new ArrayList<Track>();
            var offset = 0;
            var lastTotal = 0;
            do {
                LOGGER.debug("offset = {}", offset);
                var trackPaging = spotifyApi.getPlaylistsItems(playlist.getId())
                        .limit(MAX_PAGE_LIMIT)
                        .offset(offset)
                        .build().execute();

                lastTotal = trackPaging.getItems().length;
                offset += lastTotal;

                tracks.addAll(Arrays.stream(trackPaging.getItems())
                        .map(PlaylistTrack::getTrack)
                        .filter(track -> track instanceof se.michaelthelin.spotify.model_objects.specification.Track)
                        .map(se.michaelthelin.spotify.model_objects.specification.Track.class::cast)
                        .map(track -> new SpotifyTrack(track.getId(), track.getName(), createArtistList(track.getArtists()), createAlbum(track.getAlbum()), track.getDurationMs()))
                        .toList());
            } while (lastTotal == MAX_PAGE_LIMIT);
            
            return tracks;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            LOGGER.error("An error occurred while fetching playlist tracks", e);
            return Collections.emptyList();
        }
    }
    
    private List<SpotifyArtist> createArtistList(ArtistSimplified[] playlistArtists) {
        return Arrays.stream(playlistArtists).map(artist -> new SpotifyArtist(artist.getId(), artist.getName())).toList();
    }
    
    private SpotifyAlbum createAlbum(AlbumSimplified albumSimplified) {
        return new SpotifyAlbum(albumSimplified.getId(), albumSimplified.getName(), createArtistList(albumSimplified.getArtists()));
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

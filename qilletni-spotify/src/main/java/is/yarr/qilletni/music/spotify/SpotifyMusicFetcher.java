package is.yarr.qilletni.music.spotify;

import is.yarr.qilletni.api.music.Album;
import is.yarr.qilletni.api.music.Artist;
import is.yarr.qilletni.api.music.MusicFetcher;
import is.yarr.qilletni.api.music.Playlist;
import is.yarr.qilletni.api.music.Track;
import is.yarr.qilletni.music.spotify.auth.SpotifyAuthorizer;
import is.yarr.qilletni.music.spotify.entities.SpotifyAlbum;
import is.yarr.qilletni.music.spotify.entities.SpotifyArtist;
import is.yarr.qilletni.music.spotify.entities.SpotifyPlaylist;
import is.yarr.qilletni.music.spotify.entities.SpotifyTrack;
import is.yarr.qilletni.music.spotify.entities.SpotifyUser;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpotifyMusicFetcher implements MusicFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyMusicFetcher.class);

    private static final int MAX_PAGE_LIMIT = 50;

    private final SpotifyAuthorizer authorizer;

    public SpotifyMusicFetcher(SpotifyAuthorizer authorizer) {
        this.authorizer = authorizer;
    }

    @Override
    public Optional<Track> fetchTrack(String name, String artist) {
        LOGGER.debug("fetchTrack({}, {})", name, artist);
        try {
            var spotifyApi = authorizer.getSpotifyApi();
            var tracks = spotifyApi.searchTracks(String.format("track:%s artist:%s", name, artist))
                    .build()
                    .execute()
                    .getItems();
            
            if (tracks.length > 0) {
                return Optional.of(createTrackEntity(tracks[0]));
            }
            
            return Optional.empty();
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Track> fetchTrackById(String id) {
        LOGGER.debug("fetchTrackById({})", id);
        try {
            var spotifyApi = authorizer.getSpotifyApi();
            var track = spotifyApi.getTrack(id)
                    .build()
                    .execute();

            if (track != null) {
                return Optional.of(createTrackEntity(track));
            }
            
            return Optional.empty();
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Track> fetchTracks(List<TrackNameArtist> tracks) {
        throw new RuntimeException("fetchTracks(List<TrackNameArtist>) not supported!");
    }

    @Override
    public List<Track> fetchTracksById(List<String> trackIds) {
        LOGGER.debug("fetchTracksById({})", trackIds);
        try {
            var spotifyApi = authorizer.getSpotifyApi();
            var tracks = spotifyApi.getSeveralTracks(trackIds.toArray(String[]::new))
                    .build()
                    .execute();

            return Arrays.stream(tracks).map(SpotifyMusicFetcher::createTrackEntity).toList();
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Playlist> fetchPlaylist(String name, String author) {
        LOGGER.debug("fetchPlaylist({}, {})", name, author);
        try {
            return fetchPlaylistFromUserByName(name, author).map(SpotifyMusicFetcher::createPlaylistEntity);
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Optional<PlaylistSimplified> fetchPlaylistFromUserByName(String name, String author) throws IOException, ParseException, SpotifyWebApiException {
        var spotifyApi = authorizer.getSpotifyApi();
        return pagePlaylists(name, (limit, offset) -> spotifyApi.getListOfUsersPlaylists(author)
                .limit(MAX_PAGE_LIMIT)
                .offset(offset)
                .build()
                .execute()
                .getItems());
    }
    
    private Optional<PlaylistSimplified> pagePlaylists(String playlistName, SpotifyBiFunction<Integer, Integer, PlaylistSimplified[]> playlistFetcher) throws IOException, ParseException, SpotifyWebApiException {
        var offset = 0;
        var lastTotal = 0;

        do {
            var playlists = playlistFetcher.apply(MAX_PAGE_LIMIT, offset);

            lastTotal = playlists.length;
            offset += lastTotal;

            LOGGER.debug("playlists checking: {}", Arrays.stream(playlists).map(PlaylistSimplified::getName).collect(Collectors.joining(", ")));

            var matchingPlaylist = Arrays.stream(playlists).filter(playlist -> playlist.getName().equals(playlistName)).findFirst();
            if (matchingPlaylist.isPresent()) {
                return matchingPlaylist;
            }

            LOGGER.debug("{} == {}", lastTotal, MAX_PAGE_LIMIT);
        } while (lastTotal == MAX_PAGE_LIMIT);

        return Optional.empty();
    }

    @Override
    public Optional<Playlist> fetchPlaylistById(String id) {
        LOGGER.debug("fetchPlaylistById({})", id);
        try {
            var spotifyApi = authorizer.getSpotifyApi();
            
            var playlist = spotifyApi.getPlaylist(id).build().execute();
            
            return Optional.of(createPlaylistEntity(playlist));
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Album> fetchAlbum(String name, String artist) {
        LOGGER.debug("fetchAlbum({}, {})", name, artist);
        try {
            var spotifyApi = authorizer.getSpotifyApi();

            var albums = spotifyApi.searchAlbums(String.format("album=%s artist=%s", name, artist))
                    .build()
                    .execute()
                    .getItems();
            
            if (albums.length == 0) {
                return Optional.empty();
            }

            return Optional.of(createAlbum(albums[0]));
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Album> fetchAlbumById(String id) {
        LOGGER.debug("fetchAlbumById({})", id);
        try {
            var spotifyApi = authorizer.getSpotifyApi();

            var album = spotifyApi.getAlbum(id).build().execute();
            
            return Optional.of(createAlbum(album));
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Track> fetchAlbumTracks(Album album) {
        LOGGER.debug("fetchAlbumTracks({})", album);
        var spotifyApi = authorizer.getSpotifyApi();
        
        try {
            var tracks = new ArrayList<Track>();
            var offset = 0;
            var lastTotal = 0;
            
            do {
                LOGGER.debug("offset = {}", offset);
                var trackPaging = spotifyApi.getAlbumsTracks(album.getId())
                        .limit(MAX_PAGE_LIMIT)
                        .offset(offset)
                        .build().execute();

                lastTotal = trackPaging.getItems().length;
                offset += lastTotal;

                tracks.addAll(Arrays.stream(trackPaging.getItems())
                        .map(track -> new SpotifyTrack(track.getId(), track.getName(), createArtistList(track.getArtists()), (SpotifyAlbum) album, track.getDurationMs()))
                        .toList());
            } while (lastTotal == MAX_PAGE_LIMIT);

            LOGGER.debug("Fetched album tracks: {}", tracks);
            return tracks;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            LOGGER.error("An error occurred while fetching album tracks", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Track> fetchPlaylistTracks(Playlist playlist) { //
        LOGGER.debug("fetchPlaylistTracks({})", playlist);
        var spotifyApi = authorizer.getSpotifyApi();

        try {
            var tracks = new ArrayList<Track>();
            var offset = 0;
            var lastTotal = 0;
            do {
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
                        .map(SpotifyMusicFetcher::createTrackEntity)
                        .toList());
            } while (lastTotal == MAX_PAGE_LIMIT);

            LOGGER.debug("Fetched playlist tracks: {}", tracks);
            return tracks;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            LOGGER.error("An error occurred while fetching playlist tracks", e);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Artist> fetchArtistByName(String name) {
        LOGGER.debug("fetchArtistByName({})", name);
        try {
            var spotifyApi = authorizer.getSpotifyApi();

            var artists = spotifyApi.searchArtists(name).build().execute().getItems();
            
            if (artists.length == 0) {
                return Optional.empty();
            }

            return Optional.of(createArtist(artists[0]));
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Artist> fetchArtistById(String id) {
        LOGGER.debug("fetchArtistById({})", id);
        try {
            var spotifyApi = authorizer.getSpotifyApi();

            var artist = spotifyApi.getArtist(id).build().execute();

            return Optional.of(createArtist(artist));
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Searches for the given ID, and returns an Optional containing either a {@link Track}, {@link Playlist}, or a
     * {@link Album}.
     * 
     * @return The found object, if any
     */
    public Optional<Object> searchAnyId(String id) {
        var spotifyApi = authorizer.getSpotifyApi();

        try {
            var searched = spotifyApi.searchItem(id, "album,track,playlist").build().execute();
            
            var tracks = searched.getTracks().getItems();
            if (tracks.length > 0 && tracks[0].getId().equals(id)) {
                return Optional.of(createTrackEntity(tracks[0]));
            }
            
            var playlists = searched.getPlaylists().getItems();
            if (playlists.length > 0 && playlists[0].getId().equals(id)) {
                return Optional.of(createPlaylistEntity(playlists[0]));
            }
            
            var albums = searched.getAlbums().getItems();
            if (albums.length > 0 && albums[0].getId().equals(id)) {
                return Optional.of(createAlbum(albums[0]));
            }
            
            return Optional.empty();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    static Track createTrackEntity(se.michaelthelin.spotify.model_objects.specification.Track track) {
        return new SpotifyTrack(track.getId(), track.getName(), createArtistList(track.getArtists()), createAlbum(track.getAlbum()), track.getDurationMs());
    }

    static List<SpotifyArtist> createArtistList(ArtistSimplified[] playlistArtists) {
        return Arrays.stream(playlistArtists).map(artist -> new SpotifyArtist(artist.getId(), artist.getName())).toList();
    }

    static SpotifyAlbum createAlbum(se.michaelthelin.spotify.model_objects.specification.Album album) {
        return new SpotifyAlbum(album.getId(), album.getName(), createArtistList(album.getArtists()));
    }

    static SpotifyAlbum createAlbum(AlbumSimplified albumSimplified) {
        return new SpotifyAlbum(albumSimplified.getId(), albumSimplified.getName(), createArtistList(albumSimplified.getArtists()));
    }

    static SpotifyUser createUserEntity(se.michaelthelin.spotify.model_objects.specification.User user) {
        return new SpotifyUser(user.getId(), user.getDisplayName());
    }
    
    public static SpotifyPlaylist createPlaylistEntity(se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified playlist) {
        return new SpotifyPlaylist(playlist.getId(), playlist.getName(), createUserEntity(playlist.getOwner()), playlist.getTracks().getTotal());
    }

    public static SpotifyPlaylist createPlaylistEntity(se.michaelthelin.spotify.model_objects.specification.Playlist playlist) {
        return new SpotifyPlaylist(playlist.getId(), playlist.getName(), createUserEntity(playlist.getOwner()), playlist.getTracks().getTotal());
    }

    static SpotifyArtist createArtist(se.michaelthelin.spotify.model_objects.specification.Artist artist) {
        return new SpotifyArtist(artist.getId(), artist.getName());
    }
}

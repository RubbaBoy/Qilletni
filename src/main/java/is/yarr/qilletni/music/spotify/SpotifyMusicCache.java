package is.yarr.qilletni.music.spotify;

import is.yarr.qilletni.MapUtility;
import is.yarr.qilletni.MapUtility.Entry;
import is.yarr.qilletni.database.EntityTransaction;
import is.yarr.qilletni.music.Album;
import is.yarr.qilletni.music.Artist;
import is.yarr.qilletni.music.MusicCache;
import is.yarr.qilletni.music.MusicFetcher.TrackNameArtist;
import is.yarr.qilletni.music.Playlist;
import is.yarr.qilletni.music.Track;
import is.yarr.qilletni.music.spotify.entities.SpotifyAlbum;
import is.yarr.qilletni.music.spotify.entities.SpotifyArtist;
import is.yarr.qilletni.music.spotify.entities.SpotifyPlaylist;
import is.yarr.qilletni.music.spotify.entities.SpotifyTrack;
import is.yarr.qilletni.music.spotify.entities.SpotifyUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class SpotifyMusicCache implements MusicCache {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyMusicCache.class);

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

            Join<SpotifyTrack, SpotifyArtist> artistsJoin = root.join("artists");

            var trackNamePredicate = builder.equal(root.get("name"), name);
            var artistPredicate = builder.equal(artistsJoin.get("name"), artist);

            criteria.where(trackNamePredicate, artistPredicate);

            var tracks = session.createQuery(criteria).getResultList();

            if (!tracks.isEmpty()) {
                return Optional.of(tracks.get(0));
            }
        }

        // TODO: add to database
        return spotifyMusicFetcher.fetchTrack(name, artist);
    }

    @Override
    public Optional<Track> getTrackById(String id) {
        try (var entityTransaction = EntityTransaction.beginTransaction()) {
            var session = entityTransaction.getSession();

            var trackOptional = Optional.<Track>ofNullable(session.find(SpotifyTrack.class, id));
            if (trackOptional.isPresent()) {
                return trackOptional;
            }
        }

        // TODO: add to database
        return spotifyMusicFetcher.fetchTrackById(id);
    }

    @Override
    public List<Track> getTracks(List<TrackNameArtist> tracks) {
        // The track to look up with its destination index in the list
        var lookupTracks = new HashMap<Integer, TrackNameArtist>();
        var foundTracks = new ArrayList<Track>(tracks.size());

        try (var entityTransaction = EntityTransaction.beginTransaction()) {
            var session = entityTransaction.getSession();


            var builder = session.getCriteriaBuilder();
            var criteria = builder.createQuery(SpotifyTrack.class);
            var root = criteria.from(SpotifyTrack.class);

            Join<SpotifyTrack, SpotifyArtist> artistsJoin = root.join("artists");

            var predicateMap = new HashMap<Predicate, TrackNameArtist>();

            for (var trackNameArtist : tracks) {
                var trackNamePredicate = builder.equal(root.get("name"), trackNameArtist.name());
                var artistPredicate = builder.equal(artistsJoin.get("name"), trackNameArtist.artist());
                var combinedPredicate = builder.and(trackNamePredicate, artistPredicate);
                predicateMap.put(combinedPredicate, trackNameArtist);
            }

            criteria.where(builder.or(predicateMap.keySet().toArray(Predicate[]::new)));

            var result = session.createQuery(criteria).getResultList();
            for (var spotifyTrack : result) {
                var trackNameArtists = predicateMap.values().toArray(TrackNameArtist[]::new);
                for (int i = 0; i < trackNameArtists.length; i++) {
                    var trackNameArtist = trackNameArtists[i];
                    if (trackNameArtist.matchesTrack(spotifyTrack)) {
                        foundTracks.set(i, spotifyTrack);
                    } else {
                        lookupTracks.put(i, trackNameArtist);
                    }
                }
            }
        }

        LOGGER.debug("foundTracks = {}", foundTracks);
        LOGGER.debug("lookupTracks = {}", lookupTracks);

        var fetched = addToCache(spotifyMusicFetcher.fetchTracks(lookupTracks.values().stream().toList()));
        for (Entry(var index, var trackNameArtist) : MapUtility.getRecordEntries(lookupTracks)) {
            fetched.stream().filter(trackNameArtist::matchesTrack)
                    .findFirst()
                    .ifPresent(track -> foundTracks.set(index, track));
        }
        
        LOGGER.debug("lookupTracks = {}", lookupTracks);
        
        foundTracks.removeIf(Objects::isNull);

        LOGGER.debug("lookupTracks = {}", lookupTracks);

        // TODO: Add only looked up to database
        return foundTracks;
    }

    @Override
    public List<Track> getTracksById(List<String> trackIds) {
        // The track to look up with its destination index in the list
        var lookupTracks = new HashMap<Integer, String>();
        var foundTracks = new ArrayList<Track>(trackIds.size());

        try (var entityTransaction = EntityTransaction.beginTransaction()) {
            var session = entityTransaction.getSession();


            var builder = session.getCriteriaBuilder();
            var criteria = builder.createQuery(SpotifyTrack.class);
            var root = criteria.from(SpotifyTrack.class);

            var predicateMap = new HashMap<Predicate, String>();

            for (var id : trackIds) {
                predicateMap.put(builder.equal(root.get("id"), id), id);
            }

            criteria.where(builder.or(predicateMap.keySet().toArray(Predicate[]::new)));

            var result = session.createQuery(criteria).getResultList();
            for (var spotifyTrack : result) {
                var ids = predicateMap.values().toArray(String[]::new);
                for (int i = 0; i < ids.length; i++) {
                    var id = ids[i];
                    if (spotifyTrack.getId().equals(id)) {
                        foundTracks.set(i, spotifyTrack);
                    } else {
                        lookupTracks.put(i, id);
                    }
                }
            }
        }

        LOGGER.debug("foundTracks = {}", foundTracks);
        LOGGER.debug("lookupTracks = {}", lookupTracks);

        var fetched = addToCache(spotifyMusicFetcher.fetchTracksById(lookupTracks.values().stream().toList()));
        for (Entry(var index, var id) : MapUtility.getRecordEntries(lookupTracks)) {
            fetched.stream().filter(track -> track.getId().equals(id))
                    .findFirst()
                    .ifPresent(track -> foundTracks.set(index, track));
        }

        LOGGER.debug("lookupTracks = {}", lookupTracks);

        foundTracks.removeIf(Objects::isNull);

        LOGGER.debug("lookupTracks = {}", lookupTracks);
        
        // TODO: Add only looked up to database
        return foundTracks;
    }

    @Override
    public Optional<Playlist> getPlaylist(String name, String creator) {
        try (var entityTransaction = EntityTransaction.beginTransaction()) {
            var session = entityTransaction.getSession();

            var builder = session.getCriteriaBuilder();
            var criteria = builder.createQuery(SpotifyPlaylist.class);
            var root = criteria.from(SpotifyPlaylist.class);

            Join<SpotifyPlaylist, SpotifyUser> departmentJoin = root.join("creator");

            var playlistNamePredicate = builder.equal(root.get("title"), name);
            var creatorPredicate = builder.equal(departmentJoin.get("name"), creator);

            criteria.where(playlistNamePredicate, creatorPredicate);

            var playlists = session.createQuery(criteria).getResultList();

            if (!playlists.isEmpty()) {
                return Optional.of(playlists.get(0));
            }
        }

        // TODO: add to database
        return spotifyMusicFetcher.fetchPlaylist(name, creator);
    }

    @Override
    public Optional<Playlist> getPlaylistById(String id) {
        try (var entityTransaction = EntityTransaction.beginTransaction()) {
            var session = entityTransaction.getSession();

            var playlistOptional = Optional.<Playlist>ofNullable(session.find(SpotifyPlaylist.class, id));
            if (playlistOptional.isPresent()) {
                return playlistOptional;
            }
        }

        // TODO: add to database
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

        // TODO: add to database
        return spotifyMusicFetcher.fetchAlbum(name, artist);
    }

    @Override
    public Optional<Album> getAlbumById(String id) {
        try (var entityTransaction = EntityTransaction.beginTransaction()) {
            var session = entityTransaction.getSession();

            var albumOptional = Optional.<Album>ofNullable(session.find(SpotifyAlbum.class, id));
            if (albumOptional.isPresent()) {
                return albumOptional;
            }
        }

        // TODO: add to database
        return spotifyMusicFetcher.fetchAlbumById(id);
    }

    @Override
    public List<Track> getAlbumTracks(Album album) {
        // TODO: add to database
        return spotifyMusicFetcher.fetchAlbumTracks(album);
    }

    @Override
    public List<Track> getPlaylistTracks(Playlist playlist) {
        var playlistIndex = ((SpotifyPlaylist) playlist).getSpotifyPlaylistIndex();

        var expires = playlistIndex.getLastUpdatedIndex().toInstant().plus(7, ChronoUnit.DAYS); // when it expires
        if (expires.isAfter(Instant.now())) {
            var tracks = spotifyMusicFetcher.fetchPlaylistTracks(playlist);
            return addToCache(tracks);
        }

        return playlistIndex.getTracks().stream().map(Track.class::cast).toList();
    }

    /**
     * Attempts to put all given artists in the database. The list is assumed to be distinct. If an artist is found in
     * the database, its ID and stored instance is added to the map. A distinct artist will be added to the map no
     * matter what.
     *
     * @param artists The artists to try and put in the database
     * @return A map of all IDs and {@link SpotifyArtist} instances, regardless of if they were added to the database
     */
    private Map<String, SpotifyArtist> storeArtists(List<SpotifyArtist> artists) {
        var allArtists = new HashMap<String, SpotifyArtist>();
        try (var entityTransaction = EntityTransaction.beginTransaction()) {
            var session = entityTransaction.getSession();

            artists.parallelStream().map(artist -> {
                var foundArtist = session.find(SpotifyArtist.class, artist.getId());
                if (foundArtist != null) {
                    return foundArtist;
                }

                session.save(artist);
                return artist;
            }).forEach(artist -> allArtists.put(artist.getId(), artist));
        }

        return allArtists;
    }

    /**
     * Attempts to put all given albums in the database. The list is assumed to be distinct. If an album is found in
     * the database, its ID and stored instance is added to the map. A distinct album will be added to the map no
     * matter what. The artist map should be artists already in the database, and is used to reconstruct any
     * {@link SpotifyAlbum} object to be stored with authors that are known to be in the database.
     *
     * @param albums    The albums to try and put in the database
     * @param artistMap The artists in the database already
     * @return A map of all IDs and {@link SpotifyAlbum} instances, regardless of if they were added to the database
     */
    private Map<String, SpotifyAlbum> storeAlbums(List<SpotifyAlbum> albums, Map<String, SpotifyArtist> artistMap) {
        var allAlbums = new HashMap<String, SpotifyAlbum>();
        try (var entityTransaction = EntityTransaction.beginTransaction()) {
            var session = entityTransaction.getSession();

            albums.parallelStream().map(album -> {
                var foundAlbum = session.find(SpotifyAlbum.class, album.getId());
                if (foundAlbum != null) {
                    return foundAlbum;
                }

                // Replacing the artist list to ones that are known to be in the database
                var newArtists = album.getArtists().stream().map(Artist::getId).map(artistMap::get).toList();
                var savingAlbum = new SpotifyAlbum(album.getId(), album.getId(), newArtists);

                session.save(savingAlbum);
                return savingAlbum;
            }).forEach(album -> allAlbums.put(album.getId(), album));
        }

        return allAlbums;
    }

    /**
     * Adds all given tracks to the database (if they are not present already), using cached albums and artists for
     * everything already in the database.
     * 
     * @param addingTracks The tracks to add to the database
     * @return The list of added tracks, in the same order as addedTracks (excluding already cached ones)
     */
    private List<Track> addToCache(List<Track> addingTracks) {
        try (var entityTransaction = EntityTransaction.beginTransaction()) {
            var session = entityTransaction.getSession();
            
            var preTrackCount = addingTracks.size();
            // Remove tracks already in the database. Do this now so albums/artists are skipped for existing ones
            addingTracks = addingTracks.parallelStream().filter(track -> session.find(SpotifyTrack.class, track.getId()) == null).toList();
            
            LOGGER.debug("Removed {} tracks that are already in the database. Adding {} tracks", preTrackCount - addingTracks.size(), addingTracks.size());

            var distinctArtists = addingTracks.stream()
                    .flatMap(track -> Stream.concat(track.getArtists().stream(), track.getAlbum().getArtists().stream()))
                    .map(SpotifyArtist.class::cast)
                    .distinct().toList();

            // The instances of artists that should be saved with/are known to be in the database
            var artistMap = storeArtists(distinctArtists);

            var distinctAlbums = addingTracks.stream()
                    .map(Track::getAlbum)
                    .map(SpotifyAlbum.class::cast)
                    .distinct().toList();

            // The instances of albums that should be saved with/are known to be in the database
            var albumMap = storeAlbums(distinctAlbums, artistMap);

            return addingTracks.parallelStream().map(track -> {
                var storedArtists = track.getArtists().stream().map(Artist::getId).map(artistMap::get).toList();
                var storedAlbum = albumMap.get(track.getAlbum().getId());
                
                var newTrack = new SpotifyTrack(track.getId(), track.getName(), storedArtists, storedAlbum, track.getDuration());
                session.save(newTrack);
                
                return (Track) newTrack;
            }).toList();
        }
    }

    @Override
    public Optional<Artist> getArtistById(String id) {
        try (var entityTransaction = EntityTransaction.beginTransaction()) {
            var session = entityTransaction.getSession();

            var albumOptional = Optional.<Artist>ofNullable(session.find(SpotifyArtist.class, id));
            if (albumOptional.isPresent()) {
                return albumOptional;
            }
        }

        // TODO: add to database
        return spotifyMusicFetcher.fetchArtistById(id);
    }

    @Override
    public Optional<Artist> getArtistByName(String name) {
        try (var entityTransaction = EntityTransaction.beginTransaction()) {
            var session = entityTransaction.getSession();

            var builder = session.getCriteriaBuilder();
            var criteria = builder.createQuery(SpotifyArtist.class);
            var root = criteria.from(SpotifyArtist.class);

            criteria.where(builder.equal(root.get("name"), name));

            var artists = session.createQuery(criteria).getResultList();

            if (!artists.isEmpty()) {
                return Optional.of(artists.get(0));
            }
        }

        // TODO: add to database
        return spotifyMusicFetcher.fetchArtistByName(name);
    }
}

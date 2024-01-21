package is.yarr.qilletni.music;

import is.yarr.qilletni.api.music.MusicCache;
import is.yarr.qilletni.lang.exceptions.music.AlbumNotFoundException;
import is.yarr.qilletni.lang.exceptions.music.InvalidURLOrIDException;
import is.yarr.qilletni.lang.exceptions.music.SongNotFoundException;
import is.yarr.qilletni.lang.types.AlbumType;
import is.yarr.qilletni.lang.types.CollectionType;
import is.yarr.qilletni.lang.types.SongType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class MusicPopulator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MusicPopulator.class);
    private static final boolean EAGER_MUSIC_LOAD = "true".equals(System.getenv("EAGER_MUSIC_LOAD"));
    
    private static MusicPopulator musicPopulator;

    private final MusicCache musicCache;

    public MusicPopulator(MusicCache musicCache) {
        this.musicCache = musicCache;
        musicPopulator = this;
    }

    /**
     * If eager loading is enabled, the given song is populated. Otherwise, nothing occurs.
     * TODO: Make this into some kind of factory for SongTypes?
     * 
     * @param songType The song to populate
     * @return The supplied {@link SongType}
     */
    public SongType initiallyPopulateSong(SongType songType) {
        if (EAGER_MUSIC_LOAD) {
            populateSong(songType);
        }
        
        return songType;
    }
    
    public void populateSong(SongType songType) {
        if (songType.isSpotifyDataPopulated()) {
            return;
        }
        
        LOGGER.debug("Populating song: {}", songType);
        
        var foundTrack = switch (songType.getSongDefinition()) {
            case TITLE_ARTIST -> musicCache.getTrack(songType.getSuppliedTitle(), songType.getSuppliedArtist())
                    .orElseThrow(() -> new SongNotFoundException(String.format("Song \"%s\" by \"%s\" not found", songType.getSuppliedTitle(), songType.getSuppliedArtist())));
            case URL -> musicCache.getTrackById(getUrlId(songType.getSuppliedUrl()))
                    .orElseThrow(() -> new SongNotFoundException(String.format("Song with ID \"%s\" not found", songType.getSuppliedUrl())));
        };
        
        songType.populateSpotifyData(foundTrack);
    }

    /**
     * If eager loading is enabled, the given album is populated. Otherwise, nothing occurs.
     * TODO: Make this into some kind of factory for AlbumTypes?
     * 
     * @param albumType The album to populate
     * @return The supplied {@link AlbumType}
     */
    public AlbumType initiallyPopulateAlbum(AlbumType albumType) {
        if (EAGER_MUSIC_LOAD) {
            populateAlbum(albumType);
        }
        
        return albumType;
    }
    
    public void populateAlbum(AlbumType albumType) {
        if (albumType.isSpotifyDataPopulated()) {
            return;
        }
        
        LOGGER.debug("Populating album: {}", albumType);

        var foundAlbum = switch (albumType.getAlbumDefinition()) {
            case TITLE_ARTIST -> musicCache.getAlbum(albumType.getSuppliedTitle(), albumType.getSuppliedArtist())
                    .orElseThrow(() -> new AlbumNotFoundException(String.format("Album \"%s\" by \"%s\" not found", albumType.getSuppliedTitle(), albumType.getSuppliedArtist())));
            case URL -> musicCache.getAlbumById(getUrlId(albumType.getSuppliedUrl()))
                    .orElseThrow(() -> new AlbumNotFoundException(String.format("Album with ID \"%s\" not found", albumType.getSuppliedUrl())));
        };
        
        albumType.populateSpotifyData(foundAlbum);
    }

    /**
     * If eager loading is enabled, the given album is populated. Otherwise, nothing occurs.
     * TODO: Make this into some kind of factory for CollectionTypes?
     * 
     * @param collectionType The album to populate
     * @return The supplied {@link AlbumType}
     */
    public CollectionType initiallyPopulateCollection(CollectionType collectionType) {
        if (EAGER_MUSIC_LOAD) {
            populateCollection(collectionType);
        }
        
        return collectionType;
    }
    
    public void populateCollection(CollectionType collectionType) {
        if (collectionType.isSpotifyDataPopulated()) {
            return;
        }
        
        LOGGER.debug("Populating collection: {}", collectionType);

        var foundPlaylist = switch (collectionType.getCollectionDefinition()) {
            case NAME_CREATOR -> musicCache.getPlaylist(collectionType.getSuppliedName(), collectionType.getSuppliedCreator())
                    .orElseThrow(() -> new AlbumNotFoundException(String.format("Collection \"%s\" by \"%s\" not found", collectionType.getSuppliedName(), collectionType.getSuppliedCreator())));
            case URL -> musicCache.getPlaylistById(getUrlId(collectionType.getSuppliedUrl()))
                    .orElseThrow(() -> new AlbumNotFoundException(String.format("Album with ID \"%s\" not found", collectionType.getSuppliedUrl())));
        };
        
        collectionType.populateSpotifyData(foundPlaylist);
    }

    public static MusicPopulator getInstance() {
        return musicPopulator;
    }

    /**
     * Takes in a Spotify URL or a Spotify ID and returns the ID.
     * 
     * @param url The URL or ID
     * @return The ID
     */
    private String getUrlId(String url) {
        // Regular expression to match Spotify track URLs or an ID
        var pattern = Pattern.compile("spotify\\.com/track/(\\w{22})|^(\\w{22})$");
        var matcher = pattern.matcher(url);

        if (matcher.find()) {
            // Check which group has a match
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    return matcher.group(i);
                }
            }
        }
        
        throw new InvalidURLOrIDException(String.format("Invalid URL or ID: \"%s\"", url));
    }
}

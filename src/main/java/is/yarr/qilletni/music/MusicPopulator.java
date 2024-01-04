package is.yarr.qilletni.music;

import is.yarr.qilletni.lang.exceptions.InvalidURLException;
import is.yarr.qilletni.lang.exceptions.music.SongNotFoundException;
import is.yarr.qilletni.lang.types.SongType;
import is.yarr.qilletni.lang.types.song.SongDefinition;
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
    
    public SongType populateSong(SongType songType) {
        if (songType.getTrack() != null) {
            return songType;
        }
        
        LOGGER.info("Populating song: {}", songType);
        
        Track foundTrack;
        if (songType.getSongDefinition() == SongDefinition.TITLE_ARTIST) {
            foundTrack = musicCache.getTrack(songType.getTitle(), songType.getArtist())
                    .orElseThrow(() -> new SongNotFoundException(String.format("Song \"%s\" by \"%s\" not found", songType.getTitle(), songType.getArtist())));
        } else {
            foundTrack = musicCache.getTrackById(getUrlId(songType.getUrl()))
                    .orElseThrow(() -> new SongNotFoundException(String.format("Song with ID \"%s\" not found", songType.getUrl())));
        }
        
        songType.setTrack(foundTrack);
        return songType;
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
        
        throw new InvalidURLException(String.format("Invalid URL or ID: \"%s\"", url));
    }
}

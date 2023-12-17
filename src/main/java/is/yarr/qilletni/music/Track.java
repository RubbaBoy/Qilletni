package is.yarr.qilletni.music;

import java.util.List;

public interface Track {
    
    String getId();
    
    String getName();

    Artist getArtist();
    
    List<Artist> getArtists();
    
    Album getAlbum();

    /**
     * Duration of the track in milliseconds.
     * 
     * @return The millisecond duration of the track
     */
    int getDuration();
    
}

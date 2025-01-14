package is.yarr.qilletni.api.music;

import is.yarr.qilletni.api.auth.ServiceProvider;

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

    ServiceProvider getServiceProvider();
    
}

package dev.qilletni.api.music;

import dev.qilletni.api.auth.ServiceProvider;

import java.util.List;
import java.util.Optional;

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

    Optional<ServiceProvider> getServiceProvider();
    
}

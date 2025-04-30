package dev.qilletni.api.music;

import dev.qilletni.api.auth.ServiceProvider;

import java.util.List;
import java.util.Optional;

/**
 * A track/song that is stored in a database. The implementation of this is service provider-dependent.
 */
public interface Track {

    /**
     * The unique ID of the track. This means different things for each implementation.
     * 
     * @return The unique ID of the track
     */
    String getId();

    /**
     * The name of the track.
     * 
     * @return The name of the track
     */
    String getName();

    /**
     * The primary artist that made this track.
     * 
     * @return The primary artist that made this track
     */
    Artist getArtist();

    /**
     * Gets all artists that worked on this track.
     * 
     * @return All artists that worked on this track
     */
    List<Artist> getArtists();

    /**
     * The album this track is from.
     * 
     * @return The album this track is from
     */
    Album getAlbum();

    /**
     * Duration of the track in milliseconds.
     * 
     * @return The millisecond duration of the track
     */
    int getDuration();

    /**
     * The service provider that this track is from.
     * 
     * @return The service provider that this track is from
     */
    Optional<ServiceProvider> getServiceProvider();
    
}

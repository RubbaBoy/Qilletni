package dev.qilletni.api.music;

import dev.qilletni.api.auth.ServiceProvider;

import java.util.List;
import java.util.Optional;

/**
 * An internal album that is stored in a database. The implementation of this is service provider-dependent.
 */
public interface Album {

    /**
     * The unique ID of the album. This means different things for each implementation.
     * 
     * @return The unique ID of the album
     */
    String getId();

    /**
     * The name of the album.
     * 
     * @return The name of the album
     */
    String getName();

    /**
     * The primary artist that made this album.
     * 
     * @return The primary artist that made this album
     */
    Artist getArtist();

    /**
     * Gets all artists that worked on this album.
     * 
     * @return All artists that worked on this album
     */
    List<Artist> getArtists();

    /**
     * The service provider that this album is from.
     * 
     * @return The service provider that this album is from
     */
    Optional<ServiceProvider> getServiceProvider();
    
}

package dev.qilletni.api.music;

import dev.qilletni.api.auth.ServiceProvider;

import java.util.Optional;

/**
 * An artist that is stored in a database. The implementation of this is service provider-dependent.
 */
public interface Artist {

    /**
     * The unique ID of the artist. This means different things for each implementation.
     * 
     * @return The unique ID of the artist
     */
    String getId();

    /**
     * The name of the artist.
     * 
     * @return The name of the artist
     */
    String getName();

    /**
     * The service provider that this artist is from.
     * 
     * @return The service provider that this artist is from
     */
    Optional<ServiceProvider> getServiceProvider();
    
}

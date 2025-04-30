package dev.qilletni.api.music;

import dev.qilletni.api.auth.ServiceProvider;

import java.util.Optional;

/**
 * A user that is stored in a database. The implementation of this is service provider-dependent.
 * A user is a member on the service provider's platform, and not an artist.
 */
public interface User {

    /**
     * Retrieves the unique ID of the user. The meaning of this ID is service provider-dependent.
     *
     * @return The unique ID of the user
     */
    String getId();

    /**
     * The name of the user.
     * 
     * @return The name of the user
     */
    String getName();

    /**
     * The service provider that this track is from.
     *
     * @return The service provider that this track is from
     */
    Optional<ServiceProvider> getServiceProvider();
    
}

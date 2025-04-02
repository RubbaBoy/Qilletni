package dev.qilletni.api.music;

import dev.qilletni.api.auth.ServiceProvider;

import java.util.Optional;

public interface Artist {
    
    String getId();
    
    String getName();

    Optional<ServiceProvider> getServiceProvider();
    
}

package is.yarr.qilletni.api.music;

import is.yarr.qilletni.api.auth.ServiceProvider;

import java.util.Optional;

public interface User {
    
    String getId();
    
    String getName();

    Optional<ServiceProvider> getServiceProvider();
    
}

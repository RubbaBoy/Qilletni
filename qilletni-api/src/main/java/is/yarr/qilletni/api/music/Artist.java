package is.yarr.qilletni.api.music;

import is.yarr.qilletni.api.auth.ServiceProvider;

public interface Artist {
    
    String getId();
    
    String getName();

    ServiceProvider getServiceProvider();
    
}

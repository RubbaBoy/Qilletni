package is.yarr.qilletni.api.music;

import is.yarr.qilletni.api.auth.ServiceProvider;

import java.util.List;
import java.util.Optional;

public interface Album {

    String getId();
    
    String getName();

    Artist getArtist();
    
    List<Artist> getArtists();
    
//    TODO: This?
//    int getTrackCount();

    Optional<ServiceProvider> getServiceProvider();
    
}

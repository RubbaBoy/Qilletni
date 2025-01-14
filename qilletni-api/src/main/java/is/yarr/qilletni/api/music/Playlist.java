package is.yarr.qilletni.api.music;

import is.yarr.qilletni.api.auth.ServiceProvider;

import java.util.List;
import java.util.Optional;

public interface Playlist {
    
    String getId();
    
    String getTitle();
    
    User getCreator();
    
    int getTrackCount();

    /**
     * If the playlist was created with a pre-populated set of songs, a list of contained tracks are returned.
     * Otherwise, an empty optional will be returned, and it should be fetched via the implementation-specific way. 
     * 
     * @return An optional containing pre-populated tracks, if no additional work should be done to fetch tracks
     */
    default Optional<List<Track>> getTracks() {
        return Optional.empty();
    }

    ServiceProvider getServiceProvider();
    
}

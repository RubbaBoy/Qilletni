package is.yarr.qilletni.music;

import java.util.List;

public interface Track {
    
    String getId();
    
    String getName();
    
    String getArtist();
    
    List<String> getArtists();
    
    Album getAlbum();
    
    int getDuration();
    
}

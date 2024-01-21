package is.yarr.qilletni.api.music;

import java.util.List;

public interface Album {

    String getId();
    
    String getName();

    Artist getArtist();
    
    List<Artist> getArtists();
    
//    TODO: This?
//    int getTrackCount();
    
}

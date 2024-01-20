package is.yarr.qilletni.music;

import com.sun.xml.bind.v2.TODO;

import javax.sound.midi.Track;
import java.util.List;

public interface Album {

    String getId();
    
    String getName();

    Artist getArtist();
    
    List<Artist> getArtists();
    
//    TODO: This?
//    int getTrackCount();
    
}

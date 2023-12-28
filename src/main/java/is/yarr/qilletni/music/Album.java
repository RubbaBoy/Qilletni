package is.yarr.qilletni.music;

import javax.sound.midi.Track;
import java.util.List;

public interface Album {

    String getId();
    
    String getName();

    Artist getArtist();
    
    List<Artist> getArtists();
    
}

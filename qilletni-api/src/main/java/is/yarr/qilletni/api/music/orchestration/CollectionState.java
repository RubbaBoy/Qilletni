package is.yarr.qilletni.api.music.orchestration;

import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.music.Track;

import java.util.List;

public interface CollectionState {
    
    CollectionType getCollection();
    
    List<Track> getTracks();
    
    int getSequentialIndex();
    
    int getAndIncrementSequentialIndex();
    
    String stringValue();
    
}

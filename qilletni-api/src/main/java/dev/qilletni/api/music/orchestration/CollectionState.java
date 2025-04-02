package dev.qilletni.api.music.orchestration;

import dev.qilletni.api.lang.types.CollectionType;
import dev.qilletni.api.music.Track;

import java.util.List;

public interface CollectionState {
    
    CollectionType getCollection();
    
    List<Track> getTracks();
    
    int getSequentialIndex();
    
    int getAndIncrementSequentialIndex();
    
    String stringValue();
    
}

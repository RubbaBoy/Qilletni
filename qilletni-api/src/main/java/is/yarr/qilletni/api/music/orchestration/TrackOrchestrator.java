package is.yarr.qilletni.api.music.orchestration;

import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.lang.types.WeightsType;
import is.yarr.qilletni.api.lang.types.collection.CollectionLimit;
import is.yarr.qilletni.api.music.Track;

public interface TrackOrchestrator {

    void playTrack(Track track);
    
    void playCollection(CollectionType collectionType, boolean loop);
    
    void playCollection(CollectionType collectionType, CollectionLimit collectionLimit);
    
    Track getTrackFromCollection(CollectionType collectionType);
    
    Track getTrackFromWeight(WeightsType weightsType);
    
}

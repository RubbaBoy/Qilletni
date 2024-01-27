package is.yarr.qilletni.api.music;

import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.lang.types.WeightsType;
import is.yarr.qilletni.api.lang.types.collection.CollectionLimit;

public interface TrackOrchestrator {

    void playTrack(Track track);
    
    void playCollection(CollectionType collectionType, boolean loop);
    
    void playCollection(CollectionType collectionType, CollectionLimit collectionLimit);
    
    Track getTrackFromCollection(CollectionType collectionType);
    
    Track getTrackFromWeight(WeightsType weightsType);
    
}

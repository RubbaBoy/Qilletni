package dev.qilletni.impl.music.orchestration;

import dev.qilletni.api.lang.types.CollectionType;
import dev.qilletni.api.music.Track;
import dev.qilletni.api.music.orchestration.CollectionState;
import dev.qilletni.api.music.supplier.DynamicProvider;

import java.util.List;

public class CollectionStateImpl implements CollectionState {
    
    private final CollectionType collection;
    private final List<Track> tracks;
    private int currentIndex = 0;

    public CollectionStateImpl(CollectionType collection, DynamicProvider dynamicProvider) {
        this.collection = collection;
        this.tracks = dynamicProvider.getMusicCache().getPlaylistTracks(collection.getPlaylist());
    }
    
    @Override
    public CollectionType getCollection() {
        return collection;
    }

    @Override
    public List<Track> getTracks() {
        return tracks;
    }

    @Override
    public int getSequentialIndex() {
        return currentIndex;
    }

    @Override
    public int getAndIncrementSequentialIndex() {
        int index = currentIndex++;
        
        if (currentIndex >= tracks.size()) {
            currentIndex = 0;
        }
        
        return index;
    }

    @Override
    public String stringValue() {
        return "collection-state(index=%d, coll=%s)".formatted(currentIndex, collection.stringValue());
    }
}

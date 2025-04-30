package dev.qilletni.api.music.orchestration;

import dev.qilletni.api.lang.types.CollectionType;
import dev.qilletni.api.music.Track;

import java.util.List;

/**
 * Holds any stateful information that a collection may need for selecting its next track.
 * <br>
 * This holds an index that is incremented when a track is chosen, used for when a collection is being selected from
 * sequentially. This allows the first song to be chosen, then the second, third, etc.
 */
public interface CollectionState {

    /**
     * Retrieves the current collection associated with this state.
     *
     * @return The {@link CollectionType} representing the current collection
     */
    CollectionType getCollection();

    /**
     * Gets all the tracks the collection contains.
     * 
     * @return All tracks the collection contains
     */
    List<Track> getTracks();

    /**
     * Gets the current index that is incremented by {@link #getAndIncrementSequentialIndex()}, used for getting tracks
     * in order. This is essentially a "peek" of {@link #getAndIncrementSequentialIndex()}.
     * 
     * @return Gets the index of the current track that should be chosen
     */
    int getSequentialIndex();

    /**
     * Gets the index to choose a song by, and then increments it. If the new index goes past the length of the
     * collection, it wraps back to <code>0</code>. 
     * 
     * @return The index of the current track that should be chosen
     */
    int getAndIncrementSequentialIndex();

    /**
     * Returns a Qilletni-user-friendly string to display this state.
     * 
     * @return A string representation of this state
     */
    String stringValue();
    
}

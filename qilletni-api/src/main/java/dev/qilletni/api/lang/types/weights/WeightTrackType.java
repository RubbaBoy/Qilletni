package dev.qilletni.api.lang.types.weights;

import dev.qilletni.api.music.Playlist;

/**
 * The source of a {@link WeightEntry}.
 */
public enum WeightTrackType {

    /**
     * The weight entry is a single track.
     */
    SINGLE_TRACK,

    /**
     * The weight entry is a list of tracks, and will choose a random track from the list when the weight entry is
     * picked.
     */
    LIST,

    /**
     * The weight entry is a collection, and when the weight is chosen, it will follow the ordering rules of the
     * collection. See the <a href="https://qilletni.dev/language/types/built_in_types/#collection-weights">Collection Weights</a> docs for more info.
     */
    COLLECTION,

    /**
     * The weight entry is another set of weights, allowing for nested weights. See the <a href="https://qilletni.dev/language/types/built_in_types/#nested-weights">Nested Weights</a> docs for more info.
     */
    WEIGHTS,

    /**
     * The weight entry is a {@link Playlist}, and will choose a random track from the
     * playlist when the weight entry is picked.
     */
    PLAYLIST,

    /**
     * The weight entry is a function, and will be evaluated when the weight entry is picked. The function should
     * always supply a track. See the <a href="https://qilletni.dev/language/types/built_in_types/#function-call-weights">Function Call Weights</a> docs for more info.
     */
    FUNCTION
}

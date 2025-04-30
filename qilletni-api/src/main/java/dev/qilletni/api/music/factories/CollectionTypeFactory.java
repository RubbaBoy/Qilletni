package dev.qilletni.api.music.factories;

import dev.qilletni.api.lang.types.CollectionType;
import dev.qilletni.api.music.Playlist;

/**
 * Creates {@link CollectionType}s.
 */
public interface CollectionTypeFactory {

    /**
     * Creates a fully populated {@link CollectionType} from a given {@link Playlist}.
     * 
     * @param playlist The playlist to create the {@link CollectionType} from
     * @return The created {@link CollectionType}
     */
    CollectionType createCollectionFromTrack(Playlist playlist);
    
}

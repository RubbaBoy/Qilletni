package is.yarr.qilletni.api.music.factories;

import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.music.Playlist;

public interface CollectionTypeFactory {

    /**
     * Creates a fully populated {@link CollectionType} from a given {@link Playlist}.
     * 
     * @param playlist The playlist to create the {@link CollectionType} from
     * @return The created {@link CollectionType}
     */
    CollectionType createCollectionFromTrack(Playlist playlist);
    
}

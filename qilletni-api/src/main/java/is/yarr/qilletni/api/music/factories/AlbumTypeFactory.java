package is.yarr.qilletni.api.music.factories;

import is.yarr.qilletni.api.lang.types.AlbumType;
import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.music.Album;
import is.yarr.qilletni.api.music.Playlist;

public interface AlbumTypeFactory {

    /**
     * Creates a fully populated {@link AlbumType} from a given {@link Album}.
     * 
     * @param album The playlist to create the {@link AlbumType} from
     * @return The created {@link AlbumType}
     */
    AlbumType createAlbumFromTrack(Album album);
    
}

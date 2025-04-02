package dev.qilletni.api.music.factories;

import dev.qilletni.api.lang.types.AlbumType;
import dev.qilletni.api.music.Album;

public interface AlbumTypeFactory {

    /**
     * Creates a fully populated {@link AlbumType} from a given {@link Album}.
     * 
     * @param album The playlist to create the {@link AlbumType} from
     * @return The created {@link AlbumType}
     */
    AlbumType createAlbumFromTrack(Album album);
    
}

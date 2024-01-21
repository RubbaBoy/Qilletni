package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.types.album.AlbumDefinition;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.api.music.Album;

public non-sealed interface AlbumType extends QilletniType {
    
    AlbumDefinition getAlbumDefinition();

    void setAlbumDefinition(AlbumDefinition albumDefinition);

    String getSuppliedUrl();

    String getSuppliedTitle();

    /**
     * Returns the user-supplier artist. This should only be used for populating spotify data via
     * {@link #populateSpotifyData(Album)}.
     *
     * @return The user-supplier artist name
     */
    String getSuppliedArtist();

    EntityType getArtist(EntityDefinitionManager entityDefinitionManager);

    ListType getArtists(EntityDefinitionManager entityDefinitionManager);

    Album getAlbum();

    boolean isSpotifyDataPopulated();

    void populateSpotifyData(Album album);
}

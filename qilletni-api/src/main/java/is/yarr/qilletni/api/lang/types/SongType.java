package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.api.lang.types.song.SongDefinition;
import is.yarr.qilletni.api.music.Track;

public non-sealed interface SongType extends QilletniType {
    SongDefinition getSongDefinition();

    void setSongDefinition(SongDefinition songDefinition);

    String getSuppliedUrl();

    String getSuppliedTitle();

    String getSuppliedArtist();

    AlbumType getAlbum();

    EntityType getArtist(EntityDefinitionManager entityDefinitionManager);

    ListType getArtists(EntityDefinitionManager entityDefinitionManager);

    Track getTrack();

    boolean isSpotifyDataPopulated();

    void populateSpotifyData(Track track);
}

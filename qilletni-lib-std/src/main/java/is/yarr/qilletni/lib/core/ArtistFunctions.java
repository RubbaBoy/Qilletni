package is.yarr.qilletni.lib.core;

import is.yarr.qilletni.api.lang.types.AlbumType;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.api.lib.annotations.BeforeAnyInvocation;
import is.yarr.qilletni.api.lib.annotations.NativeOn;
import is.yarr.qilletni.api.music.MusicPopulator;

import java.util.Objects;

@NativeOn("album")
public class ArtistFunctions {

    private final MusicPopulator musicPopulator;
    private final EntityDefinitionManager entityDefinitionManager;

    public ArtistFunctions(MusicPopulator musicPopulator, EntityDefinitionManager entityDefinitionManager) {
        this.musicPopulator = musicPopulator;
        this.entityDefinitionManager = entityDefinitionManager;
    }

    @BeforeAnyInvocation
    public void setupSong(AlbumType albumType) {
        musicPopulator.populateAlbum(albumType);
    }

    public String getId(AlbumType albumType) {
        return albumType.getAlbum().getId();
    }
    
    public String getUrl(AlbumType albumType) {
        return Objects.requireNonNullElse(albumType.getSuppliedUrl(), ""); 
    }
    
    public String getName(AlbumType albumType) {
        return albumType.getAlbum().getName();
    }

    public EntityType getArtist(AlbumType albumType) {
        return albumType.getArtist(entityDefinitionManager);
    }

    public ListType getAllArtists(AlbumType albumType) {
        return albumType.getArtists(entityDefinitionManager);
    }
    
}

package dev.qilletni.lib.core;

import dev.qilletni.api.lang.types.AlbumType;
import dev.qilletni.api.lang.types.EntityType;
import dev.qilletni.api.lang.types.ListType;
import dev.qilletni.api.lang.types.entity.EntityDefinitionManager;
import dev.qilletni.api.lib.annotations.BeforeAnyInvocation;
import dev.qilletni.api.lib.annotations.NativeOn;
import dev.qilletni.api.music.MusicPopulator;

import java.util.Objects;

@NativeOn("album")
public class AlbumFunctions {
    
    private final MusicPopulator musicPopulator;
    private final EntityDefinitionManager entityDefinitionManager;

    public AlbumFunctions(MusicPopulator musicPopulator, EntityDefinitionManager entityDefinitionManager) {
        this.musicPopulator = musicPopulator;
        this.entityDefinitionManager = entityDefinitionManager;
    }

    @BeforeAnyInvocation
    public void setupSong(AlbumType albumType) {
        musicPopulator.populateAlbum(albumType);
    }

    public static String getId(AlbumType albumType) {
        return albumType.getAlbum().getId();
    }
    
    public static String getUrl(AlbumType albumType) {
        return Objects.requireNonNullElse(albumType.getSuppliedUrl(), ""); 
    }
    
    public static String getName(AlbumType albumType) {
        return albumType.getAlbum().getName();
    }

    public EntityType getArtist(AlbumType albumType) {
        return albumType.getArtist(entityDefinitionManager);
    }

    public ListType getAllArtists(AlbumType albumType) {
        return albumType.getArtists(entityDefinitionManager);
    }
    
}

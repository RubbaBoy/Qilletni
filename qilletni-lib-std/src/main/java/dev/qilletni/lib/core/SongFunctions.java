package dev.qilletni.lib.core;

import dev.qilletni.api.lang.types.AlbumType;
import dev.qilletni.api.lang.types.EntityType;
import dev.qilletni.api.lang.types.ListType;
import dev.qilletni.api.lang.types.SongType;
import dev.qilletni.api.lang.types.entity.EntityDefinitionManager;
import dev.qilletni.api.lib.annotations.BeforeAnyInvocation;
import dev.qilletni.api.lib.annotations.NativeOn;
import dev.qilletni.api.music.MusicPopulator;

import java.util.Objects;

@NativeOn("song")
public class SongFunctions {

    private final MusicPopulator musicPopulator;
    private final EntityDefinitionManager entityDefinitionManager;

    public SongFunctions(MusicPopulator musicPopulator, EntityDefinitionManager entityDefinitionManager) {
        this.musicPopulator = musicPopulator;
        this.entityDefinitionManager = entityDefinitionManager;
    }

    @BeforeAnyInvocation
    public void setupSong(SongType songType) {
        musicPopulator.populateSong(songType);
    }
    
    public String getUrl(SongType songType) {
        return Objects.requireNonNullElse(songType.getSuppliedUrl(), "");
    }
    
    public String getId(SongType songType) {
        return songType.getTrack().getId();
    }

    public String getTitle(SongType songType) {
        return songType.getTrack().getName();
    }

    public EntityType getArtist(SongType songType) {
        return songType.getArtist(entityDefinitionManager);
    }

    public ListType getAllArtists(SongType songType) {
        return songType.getArtists(entityDefinitionManager);
    }
    
    public AlbumType getAlbum(SongType songType) {
        return songType.getAlbum();
    }
    
    public int getDuration(SongType songType) {
        return songType.getTrack().getDuration();
    }
    
}

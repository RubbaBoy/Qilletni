package is.yarr.qilletni.lib.core;

import is.yarr.qilletni.api.lang.types.AlbumType;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.SongType;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.api.lib.BeforeAnyInvocation;
import is.yarr.qilletni.api.lib.NativeOn;
import is.yarr.qilletni.api.music.MusicPopulator;

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

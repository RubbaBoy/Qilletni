package is.yarr.qilletni.lib.core.nativefunctions;

import is.yarr.qilletni.lang.internal.BeforeAnyInvocation;
import is.yarr.qilletni.lang.internal.NativeOn;
import is.yarr.qilletni.api.lang.types.AlbumType;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.SongType;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.lang.types.entity.EntityDefinitionManagerImpl;
import is.yarr.qilletni.music.MusicPopulator;

import java.util.Objects;

@NativeOn("song")
public class SongFunctions {
    
    @BeforeAnyInvocation
    public static void setupSong(SongType songType) {
        MusicPopulator.getInstance().populateSong(songType);
    }
    
    public static String getUrl(SongType songType) {
        return Objects.requireNonNullElse(songType.getSuppliedUrl(), "");
    }
    
    public static String getId(SongType songType) {
        return songType.getTrack().getId();
    }

    public static String getTitle(SongType songType) {
        return songType.getTrack().getName();
    }

    public static EntityType getArtist(SongType songType) {
        return songType.getArtist(EntityDefinitionManagerImpl.getInstance());
    }

    public static ListType getAllArtists(SongType songType) {
        return songType.getArtists(EntityDefinitionManagerImpl.getInstance());
    }
    
    public static AlbumType getAlbum(SongType songType) {
        return songType.getAlbum();
    }
    
    public static int getDuration(SongType songType) {
        return songType.getTrack().getDuration();
    }
    
}

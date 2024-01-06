package is.yarr.qilletni.lib.core.nativefunctions;

import is.yarr.qilletni.lang.internal.BeforeAnyInvocation;
import is.yarr.qilletni.lang.internal.NativeOn;
import is.yarr.qilletni.lang.types.AlbumType;
import is.yarr.qilletni.lang.types.SongType;
import is.yarr.qilletni.music.MusicPopulator;

import java.util.Objects;

@NativeOn("song")
public class SongFunctions {
    
    @BeforeAnyInvocation
    public static void setupSong(SongType songType) {
        MusicPopulator.getInstance().populateSong(songType);
    }
    
    public static String getUrl(SongType songType) {
        return Objects.requireNonNullElse(songType.getUrl(), "");
    }
    
    public static String getId(SongType songType) {
        return songType.getTrack().getId();
    }
    
    public static String getArtist(SongType songType) {
        return songType.getTrack().getArtist().getName();
    }
    
    public static String getArtistId(SongType songType) {
        return songType.getTrack().getArtist().getId();
    }
    
    public static String getTitle(SongType songType) {
        return songType.getTrack().getName();
    }
    
    public static int getDuration(SongType songType) {
        return songType.getTrack().getDuration();
    }
    
    public static String toString(SongType songType) {
        return songType.stringValue();
    }
    
    public static AlbumType getAlbum(SongType songType) {
        return songType.getAlbum();
    }
    
}

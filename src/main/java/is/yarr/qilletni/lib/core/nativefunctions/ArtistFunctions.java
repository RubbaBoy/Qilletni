package is.yarr.qilletni.lib.core.nativefunctions;

import is.yarr.qilletni.lang.internal.BeforeAnyInvocation;
import is.yarr.qilletni.lang.internal.NativeOn;
import is.yarr.qilletni.api.lang.types.AlbumType;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.lang.types.entity.EntityDefinitionManagerImpl;
import is.yarr.qilletni.music.MusicPopulator;

import java.util.Objects;

@NativeOn("album")
public class ArtistFunctions {

    @BeforeAnyInvocation
    public static void setupSong(AlbumType albumType) {
        MusicPopulator.getInstance().populateAlbum(albumType);
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

    public static EntityType getArtist(AlbumType albumType) {
        return albumType.getArtist(EntityDefinitionManagerImpl.getInstance());
    }

    public static ListType getAllArtists(AlbumType albumType) {
        return albumType.getArtists(EntityDefinitionManagerImpl.getInstance());
    }
    
}

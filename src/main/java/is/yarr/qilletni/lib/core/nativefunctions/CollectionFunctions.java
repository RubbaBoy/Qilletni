package is.yarr.qilletni.lib.core.nativefunctions;

import is.yarr.qilletni.lang.internal.BeforeAnyInvocation;
import is.yarr.qilletni.lang.internal.NativeOn;
import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.lang.types.entity.EntityDefinitionManagerImpl;
import is.yarr.qilletni.music.MusicPopulator;

import java.util.Objects;

@NativeOn("collection")
public class CollectionFunctions {

    @BeforeAnyInvocation
    public static void setupSong(CollectionType collectionType) {
        MusicPopulator.getInstance().initiallyPopulateCollection(collectionType);
    }

    public static String getId(CollectionType collectionType) {
        return collectionType.getPlaylist().getId();
    }

    public static String getUrl(CollectionType collectionType) {
        return Objects.requireNonNullElse(collectionType.getSuppliedUrl(), "");
    }

    public static String getName(CollectionType collectionType) {
        return collectionType.getPlaylist().getTitle();
    }

    public static EntityType getCreator(CollectionType collectionType) {
        return collectionType.getCreator(EntityDefinitionManagerImpl.getInstance());
    }
    
    public static int getTrackCount(CollectionType collectionType) {
        return collectionType.getPlaylist().getTrackCount();
    }
    
}

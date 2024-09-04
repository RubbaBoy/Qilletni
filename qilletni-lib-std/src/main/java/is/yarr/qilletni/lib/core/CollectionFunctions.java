package is.yarr.qilletni.lib.core;

import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.api.lib.annotations.BeforeAnyInvocation;
import is.yarr.qilletni.api.lib.annotations.NativeOn;
import is.yarr.qilletni.api.music.MusicPopulator;

import java.util.Objects;

@NativeOn("collection")
public class CollectionFunctions {

    private final MusicPopulator musicPopulator;
    private final EntityDefinitionManager entityDefinitionManager;

    public CollectionFunctions(MusicPopulator musicPopulator, EntityDefinitionManager entityDefinitionManager) {
        this.musicPopulator = musicPopulator;
        this.entityDefinitionManager = entityDefinitionManager;
    }

    @BeforeAnyInvocation
    public void setupSong(CollectionType collectionType) {
        musicPopulator.initiallyPopulateCollection(collectionType);
    }

    public String getId(CollectionType collectionType) {
        return collectionType.getPlaylist().getId();
    }

    public String getUrl(CollectionType collectionType) {
        return Objects.requireNonNullElse(collectionType.getSuppliedUrl(), "");
    }

    public String getName(CollectionType collectionType) {
        return collectionType.getPlaylist().getTitle();
    }

    public EntityType getCreator(CollectionType collectionType) {
        return collectionType.getCreator(entityDefinitionManager);
    }
    
    public int getTrackCount(CollectionType collectionType) {
        return collectionType.getPlaylist().getTrackCount();
    }
    
}

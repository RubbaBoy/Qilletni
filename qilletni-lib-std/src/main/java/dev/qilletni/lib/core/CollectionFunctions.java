package dev.qilletni.lib.core;

import dev.qilletni.api.lang.internal.FunctionInvoker;
import dev.qilletni.api.lang.types.BooleanType;
import dev.qilletni.api.lang.types.CollectionType;
import dev.qilletni.api.lang.types.EntityType;
import dev.qilletni.api.lang.types.FunctionType;
import dev.qilletni.api.lang.types.StringType;
import dev.qilletni.api.lang.types.entity.EntityDefinitionManager;
import dev.qilletni.api.lib.annotations.BeforeAnyInvocation;
import dev.qilletni.api.lib.annotations.NativeOn;
import dev.qilletni.api.music.MusicCache;
import dev.qilletni.api.music.MusicPopulator;
import dev.qilletni.api.music.factories.SongTypeFactory;
import dev.qilletni.api.music.supplier.DynamicProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

@NativeOn("collection")
public class CollectionFunctions {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionFunctions.class);

    private final MusicPopulator musicPopulator;
    private final EntityDefinitionManager entityDefinitionManager;
    private final FunctionInvoker functionInvoker;
    private final SongTypeFactory songTypeFactory;
    private final MusicCache musicCache;

    public CollectionFunctions(MusicPopulator musicPopulator, EntityDefinitionManager entityDefinitionManager, FunctionInvoker functionInvoker, SongTypeFactory songTypeFactory, DynamicProvider dynamicProvider) {
        this.musicPopulator = musicPopulator;
        this.entityDefinitionManager = entityDefinitionManager;
        this.functionInvoker = functionInvoker;
        this.songTypeFactory = songTypeFactory;
        this.musicCache = dynamicProvider.getMusicCache();
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

    public boolean anySongMatches(CollectionType collectionType, FunctionType functionType) {
        var tracks = musicCache.getPlaylistTracks(collectionType.getPlaylist());
        
        return tracks.stream().anyMatch(track -> {
            var song = songTypeFactory.createSongFromTrack(track);

            return functionInvoker.invokeFunction(functionType, List.of(song)).map(result -> {
                if (result instanceof BooleanType booleanType) {
                    LOGGER.debug("Song matches: {}", song);
                    return booleanType.getValue();
                } else {
                    return false;
                }
            }).orElse(false);
        });
    }

    public boolean containsArtist(CollectionType collectionType, EntityType artistEntity) {
        var artistId = artistEntity.getEntityScope().<StringType>lookup("_id").getValue().getValue();

        LOGGER.debug("Does playlist contain artist: {}", artistId);

        var tracks = musicCache.getPlaylistTracks(collectionType.getPlaylist());

        LOGGER.debug("Tracks: {}", tracks);

        return tracks.stream()
                .anyMatch(track -> {
                    LOGGER.debug("Track \"{}\" checking {} == {}", track.getName(), track.getArtist().getId(), artistId);
                    return track.getArtist().getId().equals(artistId);
                });
    }

}

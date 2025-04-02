package dev.qilletni.lib.spotify;

import dev.qilletni.api.lang.types.CollectionType;
import dev.qilletni.api.lang.types.ListType;
import dev.qilletni.api.lang.types.SongType;
import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import dev.qilletni.api.music.factories.CollectionTypeFactory;
import dev.qilletni.lib.spotify.music.creator.PlaylistCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaylistToolsFunctions {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaylistToolsFunctions.class);
    
    private final PlaylistCreator playlistCreator;
    private final CollectionTypeFactory collectionTypeFactory;

    public PlaylistToolsFunctions(PlaylistCreator playlistCreator, CollectionTypeFactory collectionTypeFactory) {
        this.playlistCreator = playlistCreator;
        this.collectionTypeFactory = collectionTypeFactory;
    }
    
    public CollectionType createPlaylist(String name) {
        var playlist = playlistCreator.createPlaylist(name).join();
        return collectionTypeFactory.createCollectionFromTrack(playlist);
    }

    public CollectionType createPlaylist(String name, String description) {
        var playlist = playlistCreator.createPlaylist(name, description).join();
        return collectionTypeFactory.createCollectionFromTrack(playlist);
    }

    public void addToPlaylist(CollectionType collectionType, ListType songList) {
        if (!QilletniTypeClass.SONG.equals(songList.getSubType())) {
            throw new RuntimeException("addToPlaylist requires a song list, not of type " + songList.getSubType());
        }
        
        var trackList = songList.getItems().stream().map(SongType.class::cast).map(SongType::getTrack).toList();
        playlistCreator.addToPlaylist(collectionType.getPlaylist(), trackList).exceptionally(t -> {
            LOGGER.error("An error occurred while adding tracks to a playlist", t);
            return null;
        });
    }
}

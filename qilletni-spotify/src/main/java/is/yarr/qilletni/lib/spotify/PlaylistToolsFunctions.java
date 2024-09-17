package is.yarr.qilletni.lib.spotify;

import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.SongType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.api.music.factories.CollectionTypeFactory;
import is.yarr.qilletni.music.spotify.creator.PlaylistCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

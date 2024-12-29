package is.yarr.qilletni.lib.spotify;

import is.yarr.qilletni.api.lang.internal.FunctionInvoker;
import is.yarr.qilletni.api.lang.types.FunctionType;
import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.SongType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.api.lib.persistence.PackageConfig;
import is.yarr.qilletni.api.music.PlayActor;
import is.yarr.qilletni.api.music.factories.SongTypeFactory;
import is.yarr.qilletni.music.spotify.play.ReroutablePlayActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayRedirect {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayRedirect.class);
     
    private final SongTypeFactory songTypeFactory;
    private final FunctionInvoker functionInvoker;

    public PlayRedirect(SongTypeFactory songTypeFactory, FunctionInvoker functionInvoker) {
        this.songTypeFactory = songTypeFactory;
        this.functionInvoker = functionInvoker;
    }
    
    public void defaultPlay(SongType songType) {
        ReroutablePlayActor.getDefaultPlay().playTrack(songType.getTrack());
    }

    public void redirectPlayToList(ListType listType) {
        ReroutablePlayActor.setReroutedPlayTrack(track -> {
            if (!QilletniTypeClass.SONG.equals(listType.getSubType())) {
                throw new RuntimeException("Attempted to add a song to a list of type " + listType.getSubType());
            }
            
            var items = new ArrayList<>(listType.getItems());
            items.add(songTypeFactory.createSongFromTrack(track));
            listType.setItems(items);
            
            return CompletableFuture.completedFuture(PlayActor.PlayResult.SUCCESS);
        });
    }
    
    public void redirectPlayToFunction(FunctionType functionType) {
        ReroutablePlayActor.setReroutedPlayTrack(track -> {
            var songType = songTypeFactory.createSongFromTrack(track);
            
            LOGGER.debug("play func! {}", songType);
            functionInvoker.invokeFunction(functionType, List.of(songType));

            return CompletableFuture.completedFuture(PlayActor.PlayResult.SUCCESS);
        });
    }
    
    public void redirectReset() {
        ReroutablePlayActor.resetReroutedPlayTrack();
    }
}

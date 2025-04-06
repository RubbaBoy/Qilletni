package dev.qilletni.lib.core;

import dev.qilletni.api.lang.internal.FunctionInvoker;
import dev.qilletni.api.lang.types.FunctionType;
import dev.qilletni.api.lang.types.ListType;
import dev.qilletni.api.lang.types.SongType;
import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import dev.qilletni.api.lib.annotations.BeforeAnyInvocation;
import dev.qilletni.api.music.play.DefaultRoutablePlayActor;
import dev.qilletni.api.music.play.PlayActor;
import dev.qilletni.api.music.factories.SongTypeFactory;
import dev.qilletni.api.music.supplier.DynamicProvider;
import dev.qilletni.lib.core.exceptions.UnroutablePlayActorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayRedirectFunctions {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayRedirectFunctions.class);

    private final SongTypeFactory songTypeFactory;
    private final FunctionInvoker functionInvoker;
    private final DynamicProvider dynamicProvider;
    private DefaultRoutablePlayActor routablePlayActor;

    public PlayRedirectFunctions(SongTypeFactory songTypeFactory, FunctionInvoker functionInvoker, DynamicProvider dynamicProvider) {
        this.songTypeFactory = songTypeFactory;
        this.functionInvoker = functionInvoker;
        this.dynamicProvider = dynamicProvider;
    }
    
    @BeforeAnyInvocation
    public void verifyRoutable() {
        if (!(dynamicProvider.getPlayActor() instanceof DefaultRoutablePlayActor playActor)) {
            throw new UnroutablePlayActorException("Play actor for service provider is not routable");
        }
        
        this.routablePlayActor = playActor;
    }

    public void defaultPlay(SongType songType) {
        routablePlayActor.getDefaultPlay().playTrack(songType.getTrack());
    }

    public void redirectPlayToList(ListType listType) {
        routablePlayActor.setReroutedPlayTrack(track -> {
            if (!QilletniTypeClass.SONG.equals(listType.getSubType())) {
                throw new RuntimeException("Attempted to add a song to a list of type %s".formatted(listType.getSubType()));
            }

            var items = new ArrayList<>(listType.getItems());
            items.add(songTypeFactory.createSongFromTrack(track));
            listType.setItems(items);

            return CompletableFuture.completedFuture(PlayActor.PlayResult.SUCCESS);
        });
    }

    public void redirectPlayToFunction(FunctionType functionType) {
        routablePlayActor.setReroutedPlayTrack(track -> {
            var songType = songTypeFactory.createSongFromTrack(track);

            LOGGER.debug("play func! {}", songType);
            functionInvoker.invokeFunction(functionType, List.of(songType));

            return CompletableFuture.completedFuture(PlayActor.PlayResult.SUCCESS);
        });
    }

    public void redirectReset() {
        routablePlayActor.resetReroutedPlayTrack();
    }
}

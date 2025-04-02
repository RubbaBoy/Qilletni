package dev.qilletni.impl.music.factories;

import dev.qilletni.api.lang.types.SongType;
import dev.qilletni.api.music.Track;
import dev.qilletni.api.music.factories.SongTypeFactory;
import dev.qilletni.api.music.supplier.DynamicProvider;
import dev.qilletni.impl.lang.types.SongTypeImpl;

public class SongTypeFactoryImpl implements SongTypeFactory {

    private final DynamicProvider dynamicProvider;

    public SongTypeFactoryImpl(DynamicProvider dynamicProvider) {
        this.dynamicProvider = dynamicProvider;
    }

    @Override
    public SongType createSongFromTrack(Track track) {
        return new SongTypeImpl(dynamicProvider, track);
    }
}

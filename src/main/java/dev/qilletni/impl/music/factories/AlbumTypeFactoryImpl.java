package dev.qilletni.impl.music.factories;

import dev.qilletni.api.lang.types.AlbumType;
import dev.qilletni.api.music.Album;
import dev.qilletni.api.music.factories.AlbumTypeFactory;
import dev.qilletni.api.music.supplier.DynamicProvider;
import dev.qilletni.impl.lang.types.AlbumTypeImpl;

public class AlbumTypeFactoryImpl implements AlbumTypeFactory {

    private final DynamicProvider dynamicProvider;

    public AlbumTypeFactoryImpl(DynamicProvider dynamicProvider) {
        this.dynamicProvider = dynamicProvider;
    }

    @Override
    public AlbumType createAlbumFromTrack(Album album) {
        return new AlbumTypeImpl(dynamicProvider, album);
    }
}

package is.yarr.qilletni.music.factories;

import is.yarr.qilletni.api.lang.types.AlbumType;
import is.yarr.qilletni.api.music.Album;
import is.yarr.qilletni.api.music.factories.AlbumTypeFactory;
import is.yarr.qilletni.api.music.supplier.DynamicProvider;
import is.yarr.qilletni.lang.types.AlbumTypeImpl;

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

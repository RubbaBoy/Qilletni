package is.yarr.qilletni.music.factories;

import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.music.Playlist;
import is.yarr.qilletni.api.music.factories.CollectionTypeFactory;
import is.yarr.qilletni.api.music.supplier.DynamicProvider;
import is.yarr.qilletni.lang.types.CollectionTypeImpl;

public class CollectionTypeFactoryImpl implements CollectionTypeFactory {

    private final DynamicProvider dynamicProvider;

    public CollectionTypeFactoryImpl(DynamicProvider dynamicProvider) {
        this.dynamicProvider = dynamicProvider;
    }

    @Override
    public CollectionType createCollectionFromTrack(Playlist playlist) {
        return new CollectionTypeImpl(dynamicProvider, playlist);
    }
}

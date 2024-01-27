package is.yarr.qilletni.music.factories;

import is.yarr.qilletni.api.lang.types.AlbumType;
import is.yarr.qilletni.api.music.Album;
import is.yarr.qilletni.api.music.factories.AlbumTypeFactory;
import is.yarr.qilletni.lang.types.AlbumTypeImpl;

public class AlbumTypeFactoryImpl implements AlbumTypeFactory {
    
    @Override
    public AlbumType createAlbumFromTrack(Album album) {
        return new AlbumTypeImpl(album);
    }
}

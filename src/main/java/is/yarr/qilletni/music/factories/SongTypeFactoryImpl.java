package is.yarr.qilletni.music.factories;

import is.yarr.qilletni.api.lang.types.SongType;
import is.yarr.qilletni.api.music.Track;
import is.yarr.qilletni.api.music.factories.SongTypeFactory;
import is.yarr.qilletni.lang.types.SongTypeImpl;

public class SongTypeFactoryImpl implements SongTypeFactory {
    
    @Override
    public SongType createSongFromTrack(Track track) {
        return new SongTypeImpl(track);
    }
}

package is.yarr.qilletni.music.factories;

import is.yarr.qilletni.api.music.Playlist;
import is.yarr.qilletni.api.music.Track;
import is.yarr.qilletni.api.music.factories.PlaylistTypeFactory;
import is.yarr.qilletni.music.DummyPlaylist;

import java.util.List;

public class PlaylistTypeFactoryImpl implements PlaylistTypeFactory {
    
    @Override
    public Playlist createUnmodifiableDummyPlaylist(List<Track> list) {
        return new DummyPlaylist(list);
    }
}

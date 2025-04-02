package dev.qilletni.impl.music.factories;

import dev.qilletni.api.music.Playlist;
import dev.qilletni.api.music.Track;
import dev.qilletni.api.music.factories.PlaylistTypeFactory;
import dev.qilletni.impl.music.DummyPlaylist;

import java.util.List;

public class PlaylistTypeFactoryImpl implements PlaylistTypeFactory {
    
    @Override
    public Playlist createUnmodifiableDummyPlaylist(List<Track> list) {
        return new DummyPlaylist(list);
    }
}

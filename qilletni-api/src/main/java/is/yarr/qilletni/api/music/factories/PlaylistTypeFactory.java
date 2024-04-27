package is.yarr.qilletni.api.music.factories;

import is.yarr.qilletni.api.music.Playlist;
import is.yarr.qilletni.api.music.Track;

import java.util.List;

public interface PlaylistTypeFactory {

    /**
     * Creates a non-persistent {@link Playlist} with a dummy implementation from a list of songs. This has placeholder
     * names and creator, and should only be used for on-the-fly song accessing.
     * 
     * @param tracks The songs to be in the playlist
     * @return The created {@link Playlist}
     */
    Playlist createUnmodifiableDummyPlaylist(List<Track> tracks);
    
}

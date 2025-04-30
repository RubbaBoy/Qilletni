package dev.qilletni.api.music.factories;

import dev.qilletni.api.lang.types.SongType;
import dev.qilletni.api.music.Track;

/**
 * Creates {@link SongType}s.
 */
public interface SongTypeFactory {

    /**
     * Creates a fully populated {@link SongType} from a given {@link Track}.
     * 
     * @param track The track to create the {@link SongType} from
     * @return The created {@link SongType}
     */
    SongType createSongFromTrack(Track track);
    
}

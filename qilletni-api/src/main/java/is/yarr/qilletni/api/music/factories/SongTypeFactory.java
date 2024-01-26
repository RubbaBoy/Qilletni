package is.yarr.qilletni.api.music.factories;

import is.yarr.qilletni.api.lang.types.SongType;
import is.yarr.qilletni.api.music.Track;

public interface SongTypeFactory {

    /**
     * Creates a fully populated {@link SongType} from a given {@link Track}.
     * 
     * @param track The track to create the {@link SongType} from
     * @return The created {@link SongType}
     */
    SongType createSongFromTrack(Track track);
    
}

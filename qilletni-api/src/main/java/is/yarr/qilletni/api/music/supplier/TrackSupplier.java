package is.yarr.qilletni.api.music.supplier;

import is.yarr.qilletni.api.music.Track;

import java.util.List;

/**
 * An interface to represent something that can return a track.
 */
public interface TrackSupplier {

    /**
     * Gets a {@link Track} from the supplier. This is not guaranteed to be consistent.
     * 
     * @return A {@link Track}
     */
    Track getTrack();

    /**
     * Gets all possible {@link Track} values from the supplier.
     * 
     * @return All possible {@link Track}
     */
    List<Track> getAllTracks();
    
}

package is.yarr.qilletni.api.music.supplier;

import is.yarr.qilletni.api.music.Track;

import java.util.List;

/**
 * An interface to represent something that can return a track.
 */
public interface TrackSupplier {

    /**
     * Gets a {@link Track} from the supplier. The result of the supplier may return different values for every call if
     * {@link #isInconsistent()} is {@code true}.
     * 
     * @return A {@link Track}
     */
    Track getTrack();

    /**
     * Gets all possible {@link Track} values from the supplier.
     * 
     * @return All possible {@link Track}s
     */
    List<Track> getAllTracks();

    /**
     * If the {@link Track} returned via {@link #getTrack()} is inconsistent, meaning it may change.
     *
     * @return If the supplier is inconsistent
     */
    boolean isInconsistent();
    
}

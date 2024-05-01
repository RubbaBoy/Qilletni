package is.yarr.qilletni.api.music.supplier;

import is.yarr.qilletni.api.music.Track;

import java.util.function.Supplier;

/**
 * An object that represents a promise of a {@link Track}, used in weights.
 */
public interface TrackPromise extends Supplier<Track> {

    @Override
    Track get();
    
}

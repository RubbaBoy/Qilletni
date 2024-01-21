package is.yarr.qilletni.music.spotify;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

@FunctionalInterface
public interface SpotifyBiFunction<T, U, R> {

    R apply(T t, U u) throws IOException, ParseException, SpotifyWebApiException;
    
}

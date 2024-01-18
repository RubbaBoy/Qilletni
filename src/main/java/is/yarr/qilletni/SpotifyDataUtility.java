package is.yarr.qilletni;

import is.yarr.qilletni.lang.exceptions.java.UnpopulatedSpotifyDataException;

import java.util.function.Supplier;

public class SpotifyDataUtility {
    
    public static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new UnpopulatedSpotifyDataException();
        }
        
        return obj;
    }
    
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new UnpopulatedSpotifyDataException(message);
        }
        
        return obj;
    }
    
    public static <T> T requireNonNull(T obj, Supplier<String> message) {
        if (obj == null) {
            throw new UnpopulatedSpotifyDataException(message.get());
        }
        
        return obj;
    }
    
}

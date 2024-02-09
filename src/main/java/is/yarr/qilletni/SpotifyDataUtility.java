package is.yarr.qilletni;

import is.yarr.qilletni.lang.exceptions.java.UnpopulatedSpotifyDataContextException;

import java.util.function.Supplier;

public class SpotifyDataUtility {
    
    public static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new UnpopulatedSpotifyDataContextException();
        }
        
        return obj;
    }
    
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new UnpopulatedSpotifyDataContextException(message);
        }
        
        return obj;
    }
    
    public static <T> T requireNonNull(T obj, Supplier<String> message) {
        if (obj == null) {
            throw new UnpopulatedSpotifyDataContextException(message.get());
        }
        
        return obj;
    }
    
}

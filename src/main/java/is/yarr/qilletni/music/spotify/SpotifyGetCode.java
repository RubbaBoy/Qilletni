package is.yarr.qilletni.music.spotify;

import is.yarr.qilletni.music.spotify.auth.SpotifyAuthorizer;
import is.yarr.qilletni.music.spotify.auth.SpotifyPKCEAuthorizer;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

public class SpotifyGetCode {

    public static void main (String[] args) {
        SpotifyAuthorizer authorizer = SpotifyPKCEAuthorizer.createWithCodes();
        
        authorizer.authorizeSpotify().thenAccept(spotifyApi -> {
            try {
                var exec = spotifyApi.getCurrentUsersProfile().build().execute();
                System.out.println("Hello, " + exec.getDisplayName());
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
}

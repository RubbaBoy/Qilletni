package is.yarr.qilletni.music.spotify;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

public class SpotifyGetCode {

    public static void main (String[] args) throws Exception {
        var authorizer = SpotifyAuthorizer.createWithCodes();
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

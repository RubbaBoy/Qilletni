package is.yarr.qilletni.music.spotify;

import is.yarr.qilletni.api.music.Track;
import is.yarr.qilletni.music.spotify.auth.SpotifyAuthorizer;
import is.yarr.qilletni.music.spotify.auth.pkce.SpotifyPKCEAuthorizer;
import jdk.jshell.JShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.SpotifyApi;

import java.util.Scanner;
import java.util.stream.Collectors;

public class SpotifyGetCode {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyGetCode.class);

    public static SpotifyApi spotifyApi;
    public static SpotifyMusicCache cache;
    
    public static void main(String[] args) {
        SpotifyAuthorizer authorizer = SpotifyPKCEAuthorizer.createWithCodes();
        
        authorizer.authorizeSpotify().thenAccept(spotifyApi -> {
            try {
                SpotifyGetCode.spotifyApi = spotifyApi;
                SpotifyGetCode.cache = new SpotifyMusicCache(new SpotifyMusicFetcher(authorizer));
                
                var exec = spotifyApi.getCurrentUsersProfile().build().execute();
                System.out.println("Hello, " + exec.getDisplayName());
//                System.out.println("Type in commands to execute them!");
//                
//                startShell();
                
//                var playlist = cache.getPlaylist("qtest", "rubbaboy").get();
                
//                LOGGER.debug("Playlist gotten: {}", playlist);
                
                var album = cache.getAlbumById("4syaagktdRKUoKLuWQU2Y6").get();
                var tracks = cache.getAlbumTracks(album);

                System.out.println("album = " + album);

                System.out.println("tracks.size() = " + tracks.size());
                System.out.println("tracks = " + tracks.stream().map(Track::getName).collect(Collectors.joining(", ")));
            } catch (Exception e) {
                LOGGER.error("An error occurred during stuff", e);
            }
        });
    }
    
    private static void startShell() {
        try (var shell = JShell.builder().executionEngine("local").build()) {
            evalStatement(shell, "import static is.yarr.qilletni.music.spotify.SpotifyGetCode.cache;");
            evalStatement(shell, "import static is.yarr.qilletni.music.spotify.SpotifyGetCode.print;");
            shell.eval("print(cache)");

            var scanner = new Scanner(System.in);
            String line;
            while ((line = scanner.nextLine()) != null) {
                if (line.equals("quit") || line.equalsIgnoreCase("quit")) {
                    return;
                }
                
                evalStatement(shell,line);
            }
        }
    }
    
    public static void print(Object obj) {
        System.out.println("\n" + obj);
    }
    
    private static void evalStatement(JShell jShell, String statement) {
        jShell.eval(statement).forEach(l -> System.out.printf("=] %s\n", l));
    }
    
}

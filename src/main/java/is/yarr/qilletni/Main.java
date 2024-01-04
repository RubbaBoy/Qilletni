package is.yarr.qilletni;

import is.yarr.qilletni.lang.runner.QilletniProgramRunner;
import is.yarr.qilletni.music.MusicPopulator;
import is.yarr.qilletni.music.spotify.SpotifyMusicCache;
import is.yarr.qilletni.music.spotify.SpotifyMusicFetcher;
import is.yarr.qilletni.music.spotify.auth.SpotifyAuthorizer;
import is.yarr.qilletni.music.spotify.auth.SpotifyPKCEAuthorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) throws IOException {
        new Main().main(args[0]);
    }
    
    private void main(String programFile) throws IOException {
        SpotifyAuthorizer authorizer = SpotifyPKCEAuthorizer.createWithCodes();
        authorizer.authorizeSpotify().join();

        var spotifyMusicFetcher = new SpotifyMusicFetcher(authorizer);
        var musicCache = new SpotifyMusicCache(spotifyMusicFetcher);
        
        var qilletniProgramRunner = new QilletniProgramRunner(musicCache);

        qilletniProgramRunner.runProgram(Paths.get("input", programFile));
    }
}

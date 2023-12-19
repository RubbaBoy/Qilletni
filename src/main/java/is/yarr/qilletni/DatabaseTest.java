package is.yarr.qilletni;

import is.yarr.qilletni.music.spotify.SpotifyMusicCache;
import is.yarr.qilletni.music.spotify.SpotifyMusicFetcher;

public class DatabaseTest {

    public static void main(String[] args) {
        var cache = new SpotifyMusicCache(new SpotifyMusicFetcher(null));
        cache.getAlbum("Anxiety", "Breakwaters");
    }
    
}

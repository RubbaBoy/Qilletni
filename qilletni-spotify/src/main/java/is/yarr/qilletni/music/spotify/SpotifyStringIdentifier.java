package is.yarr.qilletni.music.spotify;

import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.music.Album;
import is.yarr.qilletni.api.music.Playlist;
import is.yarr.qilletni.api.music.StringIdentifier;
import is.yarr.qilletni.api.music.Track;
import is.yarr.qilletni.api.music.factories.AlbumTypeFactory;
import is.yarr.qilletni.api.music.factories.CollectionTypeFactory;
import is.yarr.qilletni.api.music.factories.SongTypeFactory;

import java.util.Optional;

public class SpotifyStringIdentifier implements StringIdentifier {
    
    private final SpotifyMusicCache spotifyMusicCache;
    private final SongTypeFactory songTypeFactory;
    private final CollectionTypeFactory collectionTypeFactory;
    private final AlbumTypeFactory albumTypeFactory;

    public SpotifyStringIdentifier(SpotifyMusicCache spotifyMusicCache, SongTypeFactory songTypeFactory, CollectionTypeFactory collectionTypeFactory, AlbumTypeFactory albumTypeFactory) {
        this.spotifyMusicCache = spotifyMusicCache;
        this.songTypeFactory = songTypeFactory;
        this.collectionTypeFactory = collectionTypeFactory;
        this.albumTypeFactory = albumTypeFactory;
    }

    @Override
    public Optional<QilletniType> parseString(String string) {
        var foundOptional = spotifyMusicCache.searchAnyId(spotifyMusicCache.getIdFromString(string));
        return foundOptional.map(found -> switch (found) {
            case Track track -> songTypeFactory.createSongFromTrack(track);
            case Playlist playlist -> collectionTypeFactory.createCollectionFromTrack(playlist);
            case Album album -> albumTypeFactory.createAlbumFromTrack(album);
            default -> throw new IllegalStateException("Unexpected value: " + found);
        });
    }
}

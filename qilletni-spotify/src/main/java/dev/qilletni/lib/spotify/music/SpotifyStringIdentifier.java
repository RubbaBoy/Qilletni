package dev.qilletni.lib.spotify.music;

import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.music.Album;
import dev.qilletni.api.music.Playlist;
import dev.qilletni.api.music.StringIdentifier;
import dev.qilletni.api.music.Track;
import dev.qilletni.api.music.factories.AlbumTypeFactory;
import dev.qilletni.api.music.factories.CollectionTypeFactory;
import dev.qilletni.api.music.factories.SongTypeFactory;

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

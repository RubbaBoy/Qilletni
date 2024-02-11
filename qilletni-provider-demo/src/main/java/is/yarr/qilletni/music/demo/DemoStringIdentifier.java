package is.yarr.qilletni.music.demo;

import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.music.StringIdentifier;
import is.yarr.qilletni.api.music.factories.AlbumTypeFactory;
import is.yarr.qilletni.api.music.factories.CollectionTypeFactory;
import is.yarr.qilletni.api.music.factories.SongTypeFactory;

import java.util.Optional;

public class DemoStringIdentifier implements StringIdentifier {

    private final DemoMusicCache demoMusicCache;
    private final SongTypeFactory songTypeFactory;
    private final CollectionTypeFactory collectionTypeFactory;
    private final AlbumTypeFactory albumTypeFactory;

    public DemoStringIdentifier(DemoMusicCache demoMusicCache, SongTypeFactory songTypeFactory, CollectionTypeFactory collectionTypeFactory, AlbumTypeFactory albumTypeFactory) {
        this.demoMusicCache = demoMusicCache;
        this.songTypeFactory = songTypeFactory;
        this.collectionTypeFactory = collectionTypeFactory;
        this.albumTypeFactory = albumTypeFactory;
    }

    @Override
    public Optional<QilletniType> parseString(String string) {
        return Optional.of(songTypeFactory.createSongFromTrack(demoMusicCache.getTrackById(string).get()));
    }
}

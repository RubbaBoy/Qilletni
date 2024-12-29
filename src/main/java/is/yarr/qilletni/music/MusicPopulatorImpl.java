package is.yarr.qilletni.music;

import is.yarr.qilletni.api.lang.types.AlbumType;
import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.lang.types.SongType;
import is.yarr.qilletni.api.lib.persistence.PackageConfig;
import is.yarr.qilletni.api.music.MusicPopulator;
import is.yarr.qilletni.api.music.supplier.DynamicProvider;
import is.yarr.qilletni.lang.exceptions.music.AlbumNotFoundException;
import is.yarr.qilletni.lang.exceptions.music.SongNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicPopulatorImpl implements MusicPopulator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MusicPopulatorImpl.class);

    private final DynamicProvider dynamicProvider;
    private final boolean eagerMusicLoad;

    public MusicPopulatorImpl(DynamicProvider dynamicProvider, PackageConfig packageConfig) {
        this.dynamicProvider = dynamicProvider;
        this.eagerMusicLoad = packageConfig.get("eagerMusicLoad").orElse("false").equals("true");
    }

    @Override
    public SongType initiallyPopulateSong(SongType songType) {
        if (eagerMusicLoad) {
            populateSong(songType);
        }
        
        return songType;
    }
    
    @Override
    public void populateSong(SongType songType) {
        if (songType.isSpotifyDataPopulated()) {
            return;
        }
        
        final var musicCache = dynamicProvider.getMusicCache();
        
        LOGGER.debug("Populating song: {}", songType);
        
        var foundTrack = switch (songType.getSongDefinition()) {
            case TITLE_ARTIST -> musicCache.getTrack(songType.getSuppliedTitle(), songType.getSuppliedArtist())
                    .orElseThrow(() -> new SongNotFoundException(String.format("Song \"%s\" by \"%s\" not found", songType.getSuppliedTitle(), songType.getSuppliedArtist())));
            case URL -> musicCache.getTrackById(musicCache.getIdFromString(songType.getSuppliedUrl()))
                    .orElseThrow(() -> new SongNotFoundException(String.format("Song with ID \"%s\" not found", songType.getSuppliedUrl())));
            case PREPOPULATED -> songType.getTrack();
        };
        
        songType.populateSpotifyData(foundTrack);
    }

    @Override
    public AlbumType initiallyPopulateAlbum(AlbumType albumType) {
        if (eagerMusicLoad) {
            populateAlbum(albumType);
        }
        
        return albumType;
    }
    
    @Override
    public void populateAlbum(AlbumType albumType) {
        if (albumType.isSpotifyDataPopulated()) {
            return;
        }

        final var musicCache = dynamicProvider.getMusicCache();
        
        LOGGER.debug("Populating album: {}", albumType);

        var foundAlbum = switch (albumType.getAlbumDefinition()) {
            case TITLE_ARTIST -> musicCache.getAlbum(albumType.getSuppliedTitle(), albumType.getSuppliedArtist())
                    .orElseThrow(() -> new AlbumNotFoundException(String.format("Album \"%s\" by \"%s\" not found", albumType.getSuppliedTitle(), albumType.getSuppliedArtist())));
            case URL -> musicCache.getAlbumById(musicCache.getIdFromString(albumType.getSuppliedUrl()))
                    .orElseThrow(() -> new AlbumNotFoundException(String.format("Album with ID \"%s\" not found", albumType.getSuppliedUrl())));
        };
        
        albumType.populateSpotifyData(foundAlbum);
    }

    @Override
    public CollectionType initiallyPopulateCollection(CollectionType collectionType) {
        if (eagerMusicLoad) {
            populateCollection(collectionType);
        }
        
        return collectionType;
    }
    
    @Override
    public void populateCollection(CollectionType collectionType) {
        if (collectionType.isSpotifyDataPopulated()) {
            return;
        }

        final var musicCache = dynamicProvider.getMusicCache();
        
        LOGGER.debug("Populating collection: {}", collectionType);

        var foundPlaylist = switch (collectionType.getCollectionDefinition()) {
            case NAME_CREATOR -> musicCache.getPlaylist(collectionType.getSuppliedName(), collectionType.getSuppliedCreator())
                    .orElseThrow(() -> new AlbumNotFoundException(String.format("Collection \"%s\" by \"%s\" not found", collectionType.getSuppliedName(), collectionType.getSuppliedCreator())));
            case URL -> musicCache.getPlaylistById(musicCache.getIdFromString(collectionType.getSuppliedUrl()))
                    .orElseThrow(() -> new AlbumNotFoundException(String.format("Album with ID \"%s\" not found", collectionType.getSuppliedUrl())));
            case PREPOPULATED, SONG_LIST -> collectionType.getPlaylist();
        };
        
        collectionType.populateSpotifyData(foundPlaylist);
    }
}

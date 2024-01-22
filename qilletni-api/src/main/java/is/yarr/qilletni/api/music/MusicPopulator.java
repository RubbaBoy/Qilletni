package is.yarr.qilletni.api.music;

import is.yarr.qilletni.api.lang.types.AlbumType;
import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.lang.types.SongType;

public interface MusicPopulator {

    /**
     * If eager loading is enabled, the given song is populated. Otherwise, nothing occurs.
     * TODO: Make this into some kind of factory for SongTypes?
     *
     * @param songType The song to populate
     * @return The supplied {@link SongType}
     */
    SongType initiallyPopulateSong(SongType songType);

    void populateSong(SongType songType);

    /**
     * If eager loading is enabled, the given album is populated. Otherwise, nothing occurs.
     * TODO: Make this into some kind of factory for AlbumTypes?
     *
     * @param albumType The album to populate
     * @return The supplied {@link AlbumType}
     */
    AlbumType initiallyPopulateAlbum(AlbumType albumType);

    void populateAlbum(AlbumType albumType);

    /**
     * If eager loading is enabled, the given album is populated. Otherwise, nothing occurs.
     * TODO: Make this into some kind of factory for CollectionTypes?
     *
     * @param collectionType The album to populate
     * @return The supplied {@link AlbumType}
     */
    CollectionType initiallyPopulateCollection(CollectionType collectionType);

    void populateCollection(CollectionType collectionType);
}

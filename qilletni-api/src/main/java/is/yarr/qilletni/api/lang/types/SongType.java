package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.api.lang.types.song.SongDefinition;
import is.yarr.qilletni.api.music.Album;
import is.yarr.qilletni.api.music.Track;

/**
 * Represents a <code>song</code> type in Qilletni. This may be created by the <code>"Song Name" by "Artist Name"</code>
 * expression syntax. The internal service provider's type is dynamic, changing how the data is fetched when the service
 * provider is changed.
 */
public non-sealed interface SongType extends AnyType {

    /**
     * Retrieves the current song definition associated with the implementing object. The song definition determines
     * the method by which song data is classified or resolved, such as by URL, title and artist, or prepopulated data.
     *
     * @return The {@link SongDefinition} representing the current song classification method
     */
    SongDefinition getSongDefinition();

    /**
     * Sets the song definition for the implementing object. The song definition determines
     * how the song data is classified or resolved, such as by URL, title and artist, or prepopulated data.
     *
     * @param songDefinition The {@link SongDefinition} representing the classification method to be set
     */
    void setSongDefinition(SongDefinition songDefinition);

    /**
     * Retrieves the supplied URL for the song, if the URL was provided.
     *
     * @return A string representing the user-supplied URL
     */
    String getSuppliedUrl();

    /**
     * Retrieves the supplied title for the album, if the title was provided.
     *
     * @return A string representing the user-supplied title
     */
    String getSuppliedTitle();

    /**
     * Retrieves the supplied artist name for the album, if the artist was provided.
     *
     * @return A string representing the user-supplied artist
     */
    String getSuppliedArtist();

    /**
     * Retrieves the album this song is on.
     * 
     * @return The album this song is on
     */
    AlbumType getAlbum();

    /**
     * Retrieves the artist as an Artist {@link EntityType} for the given entity definition manager.
     * The artist entity is resolved using the provided {@link EntityDefinitionManager}.
     *
     * @param entityDefinitionManager The manager responsible for defining and looking up entity definitions
     * @return The {@link EntityType} representing the artist resolved through the entity definition manager
     */
    EntityType getArtist(EntityDefinitionManager entityDefinitionManager);

    /**
     * Retrieves a list of artists as a {@link ListType}, resolved using the given {@link EntityDefinitionManager}.
     *
     * @param entityDefinitionManager The manager responsible for defining and resolving entity definitions
     * @return A {@link ListType} representing the artists resolved through the entity definition manager
     */
    ListType getArtists(EntityDefinitionManager entityDefinitionManager);

    /**
     * Retrieves the current track instance associated with the implementing object for the current service provider.
     * 
     * @return An instance of the {@link Track} backing the song
     */
    Track getTrack();

    /**
     * Checks whether service provider data has been successfully populated for the current instance.
     *
     * @return true if service provider data is populated, false otherwise
     */
    boolean isSpotifyDataPopulated();

    /**
     * Populates the music provider instance of the {@link Track} stored in the SongType. This must be called before
     * invoking methods that deal with the actual contents of the Track, such as getting artists.
     *
     * @param track The {@link Track} instance to populate
     */
    void populateSpotifyData(Track track);
}

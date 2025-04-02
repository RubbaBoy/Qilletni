package dev.qilletni.api.lang.types;

import dev.qilletni.api.lang.types.album.AlbumDefinition;
import dev.qilletni.api.lang.types.entity.EntityDefinitionManager;
import dev.qilletni.api.music.Album;

/**
 * Represents an <code>album</code> type in Qilletni. This may be created by the <code>"Album Name" album by "Artist Name"</code>
 * expression syntax. The internal service provider's type is dynamic, changing how the data is fetched when the service
 * provider is changed.
 */
public non-sealed interface AlbumType extends AnyType {

    /**
     * Retrieves the current album definition associated with the implementing object. The album definition determines
     * the method by which album data is classified or resolved, such as by URL, title and artist, or prepopulated data.
     *
     * @return The {@link AlbumDefinition} representing the current album classification method
     */
    AlbumDefinition getAlbumDefinition();

    /**
     * Sets the album definition for the implementing object. The album definition determines
     * how the album data is classified or resolved, such as by URL, title and artist, or prepopulated data.
     *
     * @param albumDefinition The {@link AlbumDefinition} representing the classification method to be set
     */
    void setAlbumDefinition(AlbumDefinition albumDefinition);

    /**
     * Retrieves the supplied URL for the album, if the URL was provided.
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
     * Retrieves the current album instance associated with the implementing object for the current service provider.
     *
     * @return An instance of the {@link Album} backing the album.
     */
    Album getAlbum();

    /**
     * Checks whether service provider data has been successfully populated for the current instance.
     *
     * @return true if service provider data is populated, false otherwise
     */
    boolean isSpotifyDataPopulated();

    /**
     * Populates the music provider instance of the {@link Album} stored in the AlbumType. This must be called before
     * invoking methods that deal with the actual contents of the Album, such as getting artists.
     *
     * @param album The {@link Album} instance to populate
     */
    void populateSpotifyData(Album album);
}

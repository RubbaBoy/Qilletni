package is.yarr.qilletni.api.lang.types.album;

/**
 * Specifies how an <code>album</code> is defined via its syntax or internally.
 */
public enum AlbumDefinition {
    /**
     * The album was defined with a URL or ID pointing to it on the service provider.
     */
    URL,

    /**
     * The album was defined with a title and an artist, e.g. <code>"My Album" album by "Artist Name"</code>
     */
    TITLE_ARTIST,

    /**
     * The album was defined with data already from service provider data, likely coming from a direct API call.
     */
    PREPOPULATED
}

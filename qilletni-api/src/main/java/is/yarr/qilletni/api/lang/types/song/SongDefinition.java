package is.yarr.qilletni.api.lang.types.song;

/**
 * Specifies how a <code>song</code> is defined via its syntax or internally.
 */
public enum SongDefinition {
    /**
     * The song was defined with data already from service provider data, likely coming from a direct API call.
     */
    PREPOPULATED,

    /**
     * The song was defined with a URL or ID pointing to it on the service provider.
     */
    URL,

    /**
     * The song was defined with a title and an artist, e.g. <code>"My Song" by "Artist Name"</code>
     */
    TITLE_ARTIST
}

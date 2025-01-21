package is.yarr.qilletni.api.lang.types.collection;

/**
 * Specifies how an <code>album</code> is defined via its syntax or internally.
 */
public enum CollectionDefinition {
    /**
     * The collection was defined with data already from service provider data, likely coming from a direct API call.
     */
    PREPOPULATED,

    /**
     * The collection was defined with a URL pointing to it on the service provider.
     */
    URL,

    /**
     * The collection was defined with a name and a creator's name, e.g. <code>"Collection Name" collection by "Your Name"</code>.
     */
    NAME_CREATOR,

    /**
     * The collection was defined as a list of songs, and not sourcing from any specific provider (the internal songs
     * are though).
     */
    SONG_LIST
}

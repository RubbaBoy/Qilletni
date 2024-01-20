package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.SpotifyDataUtility;
import is.yarr.qilletni.lang.types.album.AlbumDefinition;
import is.yarr.qilletni.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.music.Album;
import is.yarr.qilletni.music.Artist;

import java.util.List;

public final class AlbumType extends QilletniType {

    private AlbumDefinition albumDefinition;
    private String url;
    private String title;
    private String artist;
    
    private EntityType artistType;
    private ListType artistsType;
    private Album album;

    public AlbumType(String url) {
        this.albumDefinition = AlbumDefinition.URL;
        this.url = url;
    }

    public AlbumType(String title, String artist) {
        this.albumDefinition = AlbumDefinition.TITLE_ARTIST;
        this.title = title;
        this.artist = artist;
    }
    
    public AlbumType(Album album) {
        this.albumDefinition = AlbumDefinition.TITLE_ARTIST;
        
        populateSpotifyData(album);
    }

    public AlbumDefinition getAlbumDefinition() {
        return albumDefinition;
    }

    public void setAlbumDefinition(AlbumDefinition albumDefinition) {
        this.albumDefinition = albumDefinition;
    }

    public String getSuppliedUrl() {
        return url;
    }

    public String getSuppliedTitle() {
        return title;
    }

    /**
     * Returns the user-supplier artist. This should only be used for populating spotify data via
     * {@link #populateSpotifyData(Album)}.
     * 
     * @return The user-supplier artist name
     */
    public String getSuppliedArtist() {
        return artist;
    }

    public EntityType getArtist(EntityDefinitionManager entityDefinitionManager) {
        SpotifyDataUtility.requireNonNull(album, "Internal Album is null, #populateSpotifyData must be invoked prior to getting API data");

        if (artistType != null) {
            return artistType;
        }

        var trackArtist = album.getArtist();
        var artistEntity = entityDefinitionManager.lookup("Artist");
        return artistType = artistEntity.createInstance(new StringType(trackArtist.getId()), new StringType(trackArtist.getName()));
    }

    public ListType getArtists(EntityDefinitionManager entityDefinitionManager) {
        SpotifyDataUtility.requireNonNull(album, "Internal Album is null, #populateSpotifyData must be invoked prior to getting API data");

        if (artistsType != null) {
            return artistsType;
        }

        var trackArtists = album.getArtists();
        var artistEntity = entityDefinitionManager.lookup("Artist");
        var artistEntities = trackArtists.stream()
                .map(artist -> (QilletniType) artistEntity.createInstance(new StringType(artist.getId()), new StringType(artist.getName())))
                .toList();

        return artistsType = new ListType(QilletniTypeClass.createListOfType(artistEntity.getQilletniTypeClass()), artistEntities);
    }

    public Album getAlbum() {
        return SpotifyDataUtility.requireNonNull(album, "Internal Album is null, #populateSpotifyData must be invoked prior to getting API data");
    }

    public boolean isSpotifyDataPopulated() {
        return album != null;
    }

    public void populateSpotifyData(Album album) {
        this.album = album;
    }

    @Override
    public String stringValue() {
        if (albumDefinition == AlbumDefinition.URL) {
            return String.format("album(%s)", url);
        }

        return String.format("album(\"%s\" by \"%s\")", title, artist);
    }

    @Override
    public QilletniTypeClass<?> getTypeClass() {
        return QilletniTypeClass.ALBUM;
    }

    @Override
    public String toString() {
        return "AlbumType{" +
                "albumDefinition=" + albumDefinition +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album=" + album +
                '}';
    }
}

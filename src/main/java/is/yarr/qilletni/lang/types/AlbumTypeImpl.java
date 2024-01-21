package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.SpotifyDataUtility;
import is.yarr.qilletni.api.lang.types.AlbumType;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.album.AlbumDefinition;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.api.music.Album;

public final class AlbumTypeImpl implements AlbumType {

    private AlbumDefinition albumDefinition;
    private String url;
    private String title;
    private String artist;
    
    private EntityType artistType;
    private ListType artistsType;
    private Album album;

    public AlbumTypeImpl(String url) {
        this.albumDefinition = AlbumDefinition.URL;
        this.url = url;
    }

    public AlbumTypeImpl(String title, String artist) {
        this.albumDefinition = AlbumDefinition.TITLE_ARTIST;
        this.title = title;
        this.artist = artist;
    }
    
    public AlbumTypeImpl(Album album) {
        this.albumDefinition = AlbumDefinition.TITLE_ARTIST;
        
        populateSpotifyData(album);
    }

    @Override
    public AlbumDefinition getAlbumDefinition() {
        return albumDefinition;
    }

    @Override
    public void setAlbumDefinition(AlbumDefinition albumDefinition) {
        this.albumDefinition = albumDefinition;
    }

    @Override
    public String getSuppliedUrl() {
        return url;
    }

    @Override
    public String getSuppliedTitle() {
        return title;
    }

    @Override
    public String getSuppliedArtist() {
        return artist;
    }

    @Override
    public EntityType getArtist(EntityDefinitionManager entityDefinitionManager) {
        SpotifyDataUtility.requireNonNull(album, "Internal Album is null, #populateSpotifyData must be invoked prior to getting API data");

        if (artistType != null) {
            return artistType;
        }

        var trackArtist = album.getArtist();
        var artistEntity = entityDefinitionManager.lookup("Artist");
        return artistType = artistEntity.createInstance(new StringTypeImpl(trackArtist.getId()), new StringTypeImpl(trackArtist.getName()));
    }

    @Override
    public ListType getArtists(EntityDefinitionManager entityDefinitionManager) {
        SpotifyDataUtility.requireNonNull(album, "Internal Album is null, #populateSpotifyData must be invoked prior to getting API data");

        if (artistsType != null) {
            return artistsType;
        }

        var trackArtists = album.getArtists();
        var artistEntity = entityDefinitionManager.lookup("Artist");
        var artistEntities = trackArtists.stream()
                .map(artist -> (QilletniType) artistEntity.createInstance(new StringTypeImpl(artist.getId()), new StringTypeImpl(artist.getName())))
                .toList();

        return artistsType = new ListTypeImpl(QilletniTypeClass.createListOfType(artistEntity.getQilletniTypeClass()), artistEntities);
    }

    @Override
    public Album getAlbum() {
        return SpotifyDataUtility.requireNonNull(album, "Internal Album is null, #populateSpotifyData must be invoked prior to getting API data");
    }

    @Override
    public boolean isSpotifyDataPopulated() {
        return album != null;
    }

    @Override
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

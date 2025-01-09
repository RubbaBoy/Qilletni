package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.SpotifyDataUtility;
import is.yarr.qilletni.api.lang.types.AlbumType;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.SongType;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.api.lang.types.song.SongDefinition;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.api.music.Track;
import is.yarr.qilletni.lang.exceptions.UnsupportedOperatorException;

public final class SongTypeImpl implements SongType {
    
    private SongDefinition songDefinition;
    private String url;
    private String title;
    private String artist;

    private EntityType artistType;
    private ListType artistsType;
    private AlbumType albumType;
    private Track track;
    
    public SongTypeImpl(Track track) {
        this.songDefinition = SongDefinition.PREPOPULATED;
        this.track = track;
    }
    
    public SongTypeImpl(String url) {
        this.songDefinition = SongDefinition.URL;
        this.url = url;
    }
    
    public SongTypeImpl(String title, String artist) {
        this.songDefinition = SongDefinition.TITLE_ARTIST;
        this.title = title;
        this.artist = artist;
    }

    @Override
    public SongDefinition getSongDefinition() {
        return songDefinition;
    }

    @Override
    public void setSongDefinition(SongDefinition songDefinition) {
        this.songDefinition = songDefinition;
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
    public AlbumType getAlbum() {
        SpotifyDataUtility.requireNonNull(track, "Internal Track is null, #populateSpotifyData must be invoked prior to getting API data");

        if (albumType != null) {
            return albumType;
        }
        
        return albumType = new AlbumTypeImpl(track.getAlbum());
    }
    
    @Override
    public EntityType getArtist(EntityDefinitionManager entityDefinitionManager) {
        SpotifyDataUtility.requireNonNull(track, "Internal Track is null, #populateSpotifyData must be invoked prior to getting API data");
        
        if (artistType != null) {
            return artistType;
        }

        var trackArtist = track.getArtist();
        var artistEntity = entityDefinitionManager.lookup("Artist");
        return artistType = artistEntity.createInstance(new StringTypeImpl(trackArtist.getId()), new StringTypeImpl(trackArtist.getName()));
    }
    
    @Override
    public ListType getArtists(EntityDefinitionManager entityDefinitionManager) {
        SpotifyDataUtility.requireNonNull(track, "Internal Track is null, #populateSpotifyData must be invoked prior to getting API data");

        if (artistsType != null) {
            return artistsType;
        }
        
        var trackArtists = track.getArtists();
        var artistEntity = entityDefinitionManager.lookup("Artist");
        var artistEntities = trackArtists.stream()
                .map(artist -> (QilletniType) artistEntity.createInstance(new StringTypeImpl(artist.getId()), new StringTypeImpl(artist.getName())))
                .toList();

        return artistsType = new ListTypeImpl(QilletniTypeClass.createListOfType(artistEntity.getQilletniTypeClass()), artistEntities);
    }

    @Override
    public Track getTrack() {
        return SpotifyDataUtility.requireNonNull(track, "Internal Track is null, #populateSpotifyData must be invoked prior to getting API data");
    }
    
    @Override
    public boolean isSpotifyDataPopulated() {
        return track != null;
    }

    @Override
    public void populateSpotifyData(Track track) {
        this.track = track;
    }

    @Override
    public String stringValue() {
        if (!isSpotifyDataPopulated()) {
            if (songDefinition == SongDefinition.URL) {
                return String.format("song(%s)", url);
            }
            
            if (songDefinition == SongDefinition.TITLE_ARTIST) {
                return String.format("song(\"%s\" by \"%s\")", title, artist);
            }
        }

        return String.format("song(\"%s\" by \"%s\")", track.getName(), track.getArtist().getName());
    }

    @Override
    public QilletniType plusOperator(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "+");
    }

    @Override
    public QilletniType minusOperator(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "-");
    }

    @Override
    public QilletniTypeClass<SongType> getTypeClass() {
        return QilletniTypeClass.SONG;
    }

    @Override
    public String toString() {
        if (songDefinition == SongDefinition.URL) {
            return "SongType{url='" + url + "'}";
        }

        if (songDefinition == SongDefinition.TITLE_ARTIST) {
            return "SongType{title='" + title + "', artist='" + artist + "'}";
        }
        
        return "SongType{title='" + track.getName() + "', artist='" + track.getArtist().getName() + "'}";
    }
}

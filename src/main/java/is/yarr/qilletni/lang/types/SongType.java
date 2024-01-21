package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.SpotifyDataUtility;
import is.yarr.qilletni.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.lang.types.song.SongDefinition;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.api.music.Track;

public final class SongType extends QilletniType {
    
    private SongDefinition songDefinition;
    private String url;
    private String title;
    private String artist;

    private EntityType artistType;
    private ListType artistsType;
    private AlbumType albumType;
    private Track track;
    
    public SongType(String url) {
        this.songDefinition = SongDefinition.URL;
        this.url = url;
    }
    
    public SongType(String title, String artist) {
        this.songDefinition = SongDefinition.TITLE_ARTIST;
        this.title = title;
        this.artist = artist;
    }

    public SongDefinition getSongDefinition() {
        return songDefinition;
    }

    public void setSongDefinition(SongDefinition songDefinition) {
        this.songDefinition = songDefinition;
    }

    public String getSuppliedUrl() {
        return url;
    }

    public String getSuppliedTitle() {
        return title;
    }

    public String getSuppliedArtist() {
        return artist;
    }

    public AlbumType getAlbum() {
        SpotifyDataUtility.requireNonNull(track, "Internal Track is null, #populateSpotifyData must be invoked prior to getting API data");

        if (albumType != null) {
            return albumType;
        }
        
        return albumType = new AlbumType(track.getAlbum());
    }
    
    public EntityType getArtist(EntityDefinitionManager entityDefinitionManager) {
        SpotifyDataUtility.requireNonNull(track, "Internal Track is null, #populateSpotifyData must be invoked prior to getting API data");
        
        if (artistType != null) {
            return artistType;
        }

        var trackArtist = track.getArtist();
        var artistEntity = entityDefinitionManager.lookup("Artist");
        return artistType = artistEntity.createInstance(new StringType(trackArtist.getId()), new StringType(trackArtist.getName()));
    }
    
    public ListType getArtists(EntityDefinitionManager entityDefinitionManager) {
        SpotifyDataUtility.requireNonNull(track, "Internal Track is null, #populateSpotifyData must be invoked prior to getting API data");

        if (artistsType != null) {
            return artistsType;
        }
        
        var trackArtists = track.getArtists();
        var artistEntity = entityDefinitionManager.lookup("Artist");
        var artistEntities = trackArtists.stream()
                .map(artist -> (QilletniType) artistEntity.createInstance(new StringType(artist.getId()), new StringType(artist.getName())))
                .toList();

        return artistsType = new ListType(QilletniTypeClass.createListOfType(artistEntity.getQilletniTypeClass()), artistEntities);
    }

    public Track getTrack() {
        return SpotifyDataUtility.requireNonNull(track, "Internal Track is null, #populateSpotifyData must be invoked prior to getting API data");
    }
    
    public boolean isSpotifyDataPopulated() {
        return track != null;
    }

    public void populateSpotifyData(Track track) {
        this.track = track;
    }

    @Override
    public String stringValue() {
        if (songDefinition == SongDefinition.URL) {
            return String.format("song(%s)", url);
        }
        
        return String.format("song(\"%s\" by \"%s\")", title, artist);
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
        
        return "SongType{title='" + title + "', artist='" + artist + "'}";
    }
}

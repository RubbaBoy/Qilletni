package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.types.song.SongDefinition;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.music.Track;

public final class SongType extends QilletniType {
    
    private SongDefinition songDefinition;
    private String url;
    private String title;
    private String artist;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
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

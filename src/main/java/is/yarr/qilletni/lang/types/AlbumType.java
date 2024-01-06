package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.types.album.AlbumDefinition;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.music.Album;
import is.yarr.qilletni.music.Artist;

import java.util.List;

public final class AlbumType extends QilletniType {

    private AlbumDefinition albumDefinition;
    private String url;
    private String title;
    private List<String> artists;
    private Album album;

    public AlbumType(String url) {
        this.albumDefinition = AlbumDefinition.URL;
        this.url = url;
    }

    public AlbumType(String title, String artist) {
        this.albumDefinition = AlbumDefinition.TITLE_ARTIST;
        this.title = title;
        this.artists = List.of(artist);
    }
    
    public AlbumType(Album album) {
        this.albumDefinition = AlbumDefinition.TITLE_ARTIST;
        this.title = album.getName();
        this.artists = album.getArtists().stream().map(Artist::getName).toList();
    }

    public AlbumDefinition getAlbumDefinition() {
        return albumDefinition;
    }

    public void setAlbumDefinition(AlbumDefinition albumDefinition) {
        this.albumDefinition = albumDefinition;
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
        return artists.get(0);
    }

    public List<String> getArtists() {
        return artists;
    }

    public void setArtists(List<String> artists) {
        this.artists = artists;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    @Override
    public String stringValue() {
        if (albumDefinition == AlbumDefinition.URL) {
            return String.format("album(%s)", url);
        }

        return String.format("album(\"%s\" by \"%s\")", title, artists.get(0));
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
                ", artist='" + artists + '\'' +
                ", album=" + album +
                '}';
    }
}

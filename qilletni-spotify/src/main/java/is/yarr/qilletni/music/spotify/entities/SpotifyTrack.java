package is.yarr.qilletni.music.spotify.entities;

import is.yarr.qilletni.api.music.Album;
import is.yarr.qilletni.api.music.Artist;
import is.yarr.qilletni.api.music.Track;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import java.util.List;
import java.util.Objects;

@Entity
public class SpotifyTrack implements Track {

    @Id
    private String id;
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @OrderColumn(name="artistOrder")
    private List<SpotifyArtist> artists;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private SpotifyAlbum album;
    
    private int duration;

    public SpotifyTrack() {}

    public SpotifyTrack(String id, String name, List<SpotifyArtist> artists, SpotifyAlbum album, int duration) {
        this.id = id;
        this.name = name;
        this.artists = artists;
        this.album = album;
        this.duration = duration;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Artist getArtist() {
        return artists.getFirst();
    }

    @Override
    public List<Artist> getArtists() {
        return artists.stream().map(Artist.class::cast).toList();
    }

    @Override
    public Album getAlbum() {
        return album;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        SpotifyTrack that = (SpotifyTrack) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SpotifyTrack{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artists=" + artists +
                ", album=" + album +
                ", duration=" + duration +
                '}';
    }
}

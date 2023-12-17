package is.yarr.qilletni.music.spotify.entities;

import is.yarr.qilletni.music.Album;
import is.yarr.qilletni.music.Artist;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import java.util.List;

@Entity
public class SpotifyAlbum implements Album {
    
    @Id
    private String id;
    private String name;
    
    @ManyToMany
    @OrderColumn(name="artistOrder")
    private List<SpotifyArtist> artists;

    public SpotifyAlbum() {}

    public SpotifyAlbum(String id, String name, List<SpotifyArtist> artists) {
        this.id = id;
        this.name = name;
        this.artists = artists;
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
        return artists.get(0);
    }

    @Override
    public List<Artist> getArtists() {
        return artists.stream().map(Artist.class::cast).toList();
    }

    @Override
    public String toString() {
        return "SpotifyAlbum{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artists=" + artists +
                '}';
    }
}

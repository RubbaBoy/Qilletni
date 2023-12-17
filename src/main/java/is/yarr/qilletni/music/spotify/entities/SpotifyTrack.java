package is.yarr.qilletni.music.spotify.entities;

import is.yarr.qilletni.music.Album;
import is.yarr.qilletni.music.Artist;
import is.yarr.qilletni.music.Track;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import java.util.List;

@Entity
public class SpotifyTrack implements Track {

    @Id
    private String id;
    private String name;

    @ManyToMany
    @OrderColumn(name="artistOrder")
    private List<SpotifyArtist> artists;
    
    @ManyToOne
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
        return artists.get(0);
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
}

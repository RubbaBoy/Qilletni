package is.yarr.qilletni.music.spotify.entities;

import is.yarr.qilletni.music.Artist;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class SpotifyArtist implements Artist {
    
    @Id
    private String id;
    private String name;

    public SpotifyArtist() {}

    public SpotifyArtist(String id, String name) {
        this.id = id;
        this.name = name;
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
    public String toString() {
        return "SpotifyArtist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

package is.yarr.qilletni.music.spotify.entities;

import is.yarr.qilletni.api.auth.ServiceProvider;
import is.yarr.qilletni.api.music.Artist;
import is.yarr.qilletni.music.spotify.provider.SpotifyServiceProvider;

import javax.persistence.Entity;
import javax.persistence.Id;

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
    public ServiceProvider getServiceProvider() {
        return SpotifyServiceProvider.getServiceProviderInstance();
    }

    @Override
    public String toString() {
        return "SpotifyArtist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

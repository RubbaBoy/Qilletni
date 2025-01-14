package is.yarr.qilletni.music.spotify.entities;

import is.yarr.qilletni.api.auth.ServiceProvider;
import is.yarr.qilletni.api.music.User;
import is.yarr.qilletni.music.spotify.provider.SpotifyServiceProvider;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Optional;

@Entity
public class SpotifyUser implements User {
    
    @Id
    private String id;
    private String name;

    public SpotifyUser() {}

    public SpotifyUser(String id, String name) {
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
    public Optional<ServiceProvider> getServiceProvider() {
        return Optional.ofNullable(SpotifyServiceProvider.getServiceProviderInstance());
    }

    @Override
    public String toString() {
        return "SpotifyUser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

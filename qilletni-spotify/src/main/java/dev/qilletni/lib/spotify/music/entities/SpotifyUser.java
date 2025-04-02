package dev.qilletni.lib.spotify.music.entities;

import dev.qilletni.api.auth.ServiceProvider;
import dev.qilletni.api.music.User;
import dev.qilletni.lib.spotify.music.provider.SpotifyServiceProvider;

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

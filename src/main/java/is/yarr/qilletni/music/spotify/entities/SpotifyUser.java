package is.yarr.qilletni.music.spotify.entities;

import is.yarr.qilletni.music.User;

import javax.persistence.Id;

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
    public String toString() {
        return "SpotifyUser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

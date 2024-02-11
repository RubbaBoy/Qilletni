package is.yarr.qilletni.music.demo.entities;

import is.yarr.qilletni.api.music.Artist;

public class DemoArtist implements Artist {

    private final String id;
    private final String name;

    public DemoArtist(String id, String name) {
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
        return "DemoArtist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

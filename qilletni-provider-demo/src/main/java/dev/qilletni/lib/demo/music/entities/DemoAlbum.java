package dev.qilletni.lib.demo.music.entities;

import dev.qilletni.api.music.Album;
import dev.qilletni.api.music.Artist;

import java.util.List;

public class DemoAlbum implements Album {

    private final String id;
    private final String name;
    private final List<Artist> artists;
    
    private List<DemoTrack> tracks;

    public DemoAlbum(String id, String name, List<Artist> artists) {
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
        return artists;
    } 

    public List<DemoTrack> getTracks() {
        return tracks;
    }

    public void setTracks(List<DemoTrack> tracks) {
        this.tracks = tracks;
    }

    @Override
    public String toString() {
        return "DemoAlbum{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artists=" + artists +
                ", tracks=" + tracks +
                '}';
    }
}

package dev.qilletni.lib.spotify.music.entities;

import dev.qilletni.api.auth.ServiceProvider;
import dev.qilletni.api.music.Playlist;
import dev.qilletni.api.music.User;
import dev.qilletni.lib.spotify.music.provider.SpotifyServiceProvider;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.sql.Date;
import java.util.Collections;
import java.util.Optional;

@Entity
public class SpotifyPlaylist implements Playlist {
    
    @Id
    private String id;
    private String title;
    private int trackCount;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private SpotifyUser creator;
    
    @Embedded
    private SpotifyPlaylistIndex spotifyPlaylistIndex;

    public SpotifyPlaylist() {}

    public SpotifyPlaylist(String id, String title, SpotifyUser creator, int trackCount) {
        this.id = id;
        this.title = title;
        this.creator = creator;
        this.trackCount = trackCount;
        this.spotifyPlaylistIndex = new SpotifyPlaylistIndex(Collections.emptyList(), new Date(0));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public User getCreator() {
        return creator;
    }

    @Override
    public int getTrackCount() {
        return trackCount;
    }

    @Override
    public Optional<ServiceProvider> getServiceProvider() {
        return Optional.ofNullable(SpotifyServiceProvider.getServiceProviderInstance());
    }

    public SpotifyPlaylistIndex getSpotifyPlaylistIndex() {
        return spotifyPlaylistIndex;
    }

    public void setSpotifyPlaylistIndex(SpotifyPlaylistIndex spotifyPlaylistIndex) {
        this.spotifyPlaylistIndex = spotifyPlaylistIndex;
    }

    @Override
    public String toString() {
        return "SpotifyPlaylist{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", creator=" + creator +
                '}';
    }
}

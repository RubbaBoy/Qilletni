package is.yarr.qilletni.music.spotify.entities;

import is.yarr.qilletni.music.Playlist;
import is.yarr.qilletni.music.User;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class SpotifyPlaylist implements Playlist {
    
    @Id
    private String id;
    private String title;
    
    @ManyToOne
    private SpotifyUser creator;
    
    @Embedded
    private SpotifyPlaylistIndex spotifyPlaylistIndex;

    public SpotifyPlaylist() {}

    public SpotifyPlaylist(String id, String title, SpotifyUser creator) {
        this.id = id;
        this.title = title;
        this.creator = creator;
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
        return spotifyPlaylistIndex.getTracks().size();
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
                ", trackCount=" + spotifyPlaylistIndex.getTracks().size() +
                '}';
    }
}

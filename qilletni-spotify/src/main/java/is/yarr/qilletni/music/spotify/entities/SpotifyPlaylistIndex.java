package is.yarr.qilletni.music.spotify.entities;

import javax.persistence.Embeddable;
import javax.persistence.ManyToMany;
import java.sql.Date;
import java.util.List;

/**
 * An Entity that stores a cached index of tracks in the playlist. This is meant to regularly be deleted.
 */
@Embeddable
public class SpotifyPlaylistIndex {
    
    @ManyToMany
    private List<SpotifyTrack> tracks;
    
    private Date lastUpdatedIndex;

    public SpotifyPlaylistIndex() {}

    public SpotifyPlaylistIndex(List<SpotifyTrack> tracks, Date lastUpdatedIndex) {
        this.tracks = tracks;
        this.lastUpdatedIndex = lastUpdatedIndex;
    }

    public List<SpotifyTrack> getTracks() {
        return tracks;
    }

    public Date getLastUpdatedIndex() {
        return lastUpdatedIndex;
    }

    @Override
    public String toString() {
        return "SpotifyPlaylistIndex{" +
                "tracks=" + tracks +
                ", lastUpdatedIndex=" + lastUpdatedIndex +
                '}';
    }
}

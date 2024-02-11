package is.yarr.qilletni.music.demo.entities;

import java.util.Date;
import java.util.List;

public class DemoPlaylistIndex {
    
    private List<DemoTrack> tracks;
    private Date lastUpdatedIndex;

    public DemoPlaylistIndex(List<DemoTrack> tracks, Date lastUpdatedIndex) {
        this.tracks = tracks;
        this.lastUpdatedIndex = lastUpdatedIndex;
    }

    public List<DemoTrack> getTracks() {
        return tracks;
    }

    public Date getLastUpdatedIndex() {
        return lastUpdatedIndex;
    }

    @Override
    public String toString() {
        return "DemoPlaylistIndex{" +
                "tracks=" + tracks +
                ", lastUpdatedIndex=" + lastUpdatedIndex +
                '}';
    }
}

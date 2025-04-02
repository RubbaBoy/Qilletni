package dev.qilletni.lib.demo.music.entities;

import dev.qilletni.api.music.Playlist;
import dev.qilletni.api.music.User;

public class DemoPlaylist implements Playlist {
    
    private final String id;
    private final String title;
    private final int trackCount;
    private final User creator;

    private DemoPlaylistIndex demoPlaylistIndex;

    public DemoPlaylist(String id, String title, int trackCount, User creator) {
        this.id = id;
        this.title = title;
        this.trackCount = trackCount;
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
        return trackCount;
    }
    
    public DemoPlaylistIndex getDemoPlaylistIndex() {
        return demoPlaylistIndex;
    }
    
    public void setDemoPlaylistIndex(DemoPlaylistIndex demoPlaylistIndex) {
        this.demoPlaylistIndex = demoPlaylistIndex;
    }

    @Override
    public String toString() {
        return "DemoPlaylist{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", trackCount=" + trackCount +
                ", creator=" + creator +
                '}';
    }
}

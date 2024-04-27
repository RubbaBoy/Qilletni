package is.yarr.qilletni.music;

import is.yarr.qilletni.api.music.Playlist;
import is.yarr.qilletni.api.music.Track;
import is.yarr.qilletni.api.music.User;

import java.util.List;
import java.util.Optional;

public class DummyPlaylist implements Playlist {
    
    private final List<Track> tracks;
    private final String identifier;
    private final User user;

    public DummyPlaylist(List<Track> tracks) {
        this.tracks = tracks;
        this.identifier = "playlist-%d".formatted(tracks.hashCode());
        this.user = new DummyUser();
    }

    @Override
    public String getId() {
        return identifier;
    }

    @Override
    public String getTitle() {
        return identifier;
    }

    @Override
    public User getCreator() {
        return user;
    }

    @Override
    public int getTrackCount() {
        return tracks.size();
    }

    @Override
    public Optional<List<Track>> getTracks() {
        return Optional.of(tracks);
    }
}

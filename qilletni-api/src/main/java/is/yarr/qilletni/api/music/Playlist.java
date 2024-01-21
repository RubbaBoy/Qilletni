package is.yarr.qilletni.api.music;

public interface Playlist {
    
    String getId();
    
    String getTitle();
    
    User getCreator();
    
    int getTrackCount();
    
}

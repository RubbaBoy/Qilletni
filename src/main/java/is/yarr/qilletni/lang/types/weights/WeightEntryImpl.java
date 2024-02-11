package is.yarr.qilletni.lang.types.weights;

import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.WeightsType;
import is.yarr.qilletni.api.lang.types.weights.WeightEntry;
import is.yarr.qilletni.api.lang.types.weights.WeightTrackType;
import is.yarr.qilletni.api.lang.types.weights.WeightUnit;
import is.yarr.qilletni.api.lang.types.SongType;
import is.yarr.qilletni.api.music.Playlist;
import is.yarr.qilletni.api.music.Track;
import is.yarr.qilletni.api.music.supplier.DynamicProvider;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WeightEntryImpl implements WeightEntry {
    private double weightAmount;
    private WeightUnit weightUnit;
    private boolean canRepeatTrack;
    private boolean canRepeatWeight;

    private final WeightTrackType weightTrackType;
    private SongType song;
    private ListType songList;
    private CollectionType collection;
    private WeightsType weights;
    private Playlist playlist;
    private DynamicProvider dynamicProvider;

    public WeightEntryImpl(int weightAmount, WeightUnit weightUnit, SongType song, boolean canRepeatTrack, boolean canRepeatWeight) {
        this.weightAmount = weightAmount;
        this.weightUnit = weightUnit;
        this.canRepeatTrack = canRepeatTrack;
        this.canRepeatWeight = canRepeatWeight;
        
        this.song = song;
        this.weightTrackType = WeightTrackType.SINGLE_TRACK;
    }

    public WeightEntryImpl(int weightAmount, WeightUnit weightUnit, ListType songList, boolean canRepeatTrack, boolean canRepeatWeight) {
        this.weightAmount = weightAmount;
        this.weightUnit = weightUnit;
        this.canRepeatTrack = canRepeatTrack;
        this.canRepeatWeight = canRepeatWeight;
        
        this.songList = songList;
        this.weightTrackType = WeightTrackType.LIST;
    }

    public WeightEntryImpl(int weightAmount, WeightUnit weightUnit, DynamicProvider dynamicProvider, CollectionType collection, boolean canRepeatTrack, boolean canRepeatWeight) {
        this.weightAmount = weightAmount;
        this.weightUnit = weightUnit;
        this.dynamicProvider = dynamicProvider;
        this.canRepeatTrack = canRepeatTrack;
        this.canRepeatWeight = canRepeatWeight;

        this.collection = collection;
        this.weightTrackType = WeightTrackType.COLLECTION;
    }

    public WeightEntryImpl(int weightAmount, WeightUnit weightUnit, DynamicProvider dynamicProvider, WeightsType weights, boolean canRepeatTrack, boolean canRepeatWeight) {
        this.weightAmount = weightAmount;
        this.weightUnit = weightUnit;
        this.dynamicProvider = dynamicProvider;
        this.canRepeatTrack = canRepeatTrack;
        this.canRepeatWeight = canRepeatWeight;

        this.weights = weights;
        this.weightTrackType = WeightTrackType.WEIGHTS;
    }

    public WeightEntryImpl(int weightAmount, WeightUnit weightUnit, DynamicProvider dynamicProvider, Playlist playlist, boolean canRepeatTrack, boolean canRepeatWeight) {
        this.weightAmount = weightAmount;
        this.weightUnit = weightUnit;
        this.dynamicProvider = dynamicProvider;
        this.canRepeatTrack = canRepeatTrack;
        this.canRepeatWeight = canRepeatWeight;
        
        this.playlist = playlist;
        this.weightTrackType = WeightTrackType.PLAYLIST;
    }

    @Override
    public double getWeightAmount() {
        return weightAmount;
    }

    @Override
    public void setWeightAmount(double weightAmount) {
        this.weightAmount = weightAmount;
    }

    @Override
    public WeightUnit getWeightUnit() {
        return weightUnit;
    }

    @Override
    public void setWeightUnit(WeightUnit weightUnit) {
        this.weightUnit = weightUnit;
    }

    @Override
    public void setCanRepeat(boolean canRepeatTrack) {
        this.canRepeatTrack = canRepeatTrack;
    }

    @Override
    public boolean getCanRepeatTrack() {
        return canRepeatTrack;
    }

    @Override
    public void setCanRepeatWeight(boolean canRepeatWeight) {
        this.canRepeatWeight = canRepeatWeight;
    }

    @Override
    public boolean getCanRepeatWeight() {
        return canRepeatWeight;
    }

    @Override
    public WeightTrackType getTrackType() {
        return weightTrackType;
    }

    @Override
    public Track getTrack() {
        final var trackOrchestrator = dynamicProvider.getTrackOrchestrator();
        final var musicCache = dynamicProvider.getMusicCache();
        
        return switch (weightTrackType) {
            case SINGLE_TRACK -> song.getTrack();
            case LIST -> {
                var songs = songList.getItems();
                yield ((SongType) songs.get(ThreadLocalRandom.current().nextInt(0, songs.size()))).getTrack();
            }
            case COLLECTION -> trackOrchestrator.getTrackFromCollection(collection);
            case WEIGHTS -> trackOrchestrator.getTrackFromWeight(weights);
            case PLAYLIST -> {
                var tracks = musicCache.getPlaylistTracks(playlist);
                yield tracks.get(ThreadLocalRandom.current().nextInt(0, tracks.size()));
            }
        };
    }

    @Override
    public List<Track> getAllTracks() {
        final var musicCache = dynamicProvider.getMusicCache();
        
        return switch (weightTrackType) {
            case SINGLE_TRACK -> List.of(song.getTrack());
            case LIST -> songList.getItems().stream().map(SongType.class::cast).map(SongType::getTrack).toList();
            case COLLECTION -> musicCache.getPlaylistTracks(collection.getPlaylist());
            case WEIGHTS -> weights.getWeightEntries().stream().flatMap(weightEntry -> weightEntry.getAllTracks().stream()).toList();
            case PLAYLIST -> musicCache.getPlaylistTracks(playlist);
        };
    }

    @Override
    public String getTrackStringValue() {
        return switch (weightTrackType) {
            case SINGLE_TRACK -> song.stringValue();
            case LIST -> songList.stringValue();
            case COLLECTION -> collection.stringValue();
            case WEIGHTS -> weights.stringValue();
            case PLAYLIST -> String.format("[Raw playlist of ID %s]", playlist.getId());
        };
    }

    @Override
    public String toString() {
        return "WeightEntry[" + weightAmount + weightUnit.getStringUnit() + " " + getTrackStringValue() + "]";
    }
}

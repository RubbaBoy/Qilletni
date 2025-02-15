package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.SpotifyDataUtility;
import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.WeightsType;
import is.yarr.qilletni.api.lang.types.collection.CollectionDefinition;
import is.yarr.qilletni.api.lang.types.collection.CollectionOrder;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.api.music.Playlist;
import is.yarr.qilletni.api.music.Track;
import is.yarr.qilletni.api.music.supplier.DynamicProvider;
import is.yarr.qilletni.lang.exceptions.UnsupportedOperatorException;
import is.yarr.qilletni.music.DummyPlaylist;

import java.util.List;

public final class CollectionTypeImpl implements CollectionType {

    private CollectionDefinition collectionDefinition;
    private String url;
    private String name;
    private String creator;
    private CollectionOrder order = CollectionOrder.SHUFFLE;
    private WeightsType weights;
    private final DynamicMusicType<Playlist> dynamicPlaylist;
    private EntityType creatorType;
    
    public CollectionTypeImpl(DynamicProvider dynamicProvider, Playlist playlist) {
        this.collectionDefinition = CollectionDefinition.PREPOPULATED;
        this.dynamicPlaylist = new DynamicMusicType<>(Playlist.class, dynamicProvider, playlist);
    }

    public CollectionTypeImpl(DynamicProvider dynamicProvider, String url) {
        this.collectionDefinition = CollectionDefinition.URL;
        this.url = url;
        this.dynamicPlaylist = new DynamicMusicType<>(Playlist.class, dynamicProvider);
    }

    public CollectionTypeImpl(DynamicProvider dynamicProvider, String name, String creator) {
        this.collectionDefinition = CollectionDefinition.NAME_CREATOR;
        this.name = name;
        this.creator = creator;
        this.dynamicPlaylist = new DynamicMusicType<>(Playlist.class, dynamicProvider);
    }
    
    public CollectionTypeImpl(DynamicProvider dynamicProvider, List<Track> tracks) {
        this.collectionDefinition = CollectionDefinition.SONG_LIST;
        this.dynamicPlaylist = new DynamicMusicType<>(Playlist.class, dynamicProvider, new DummyPlaylist(tracks));
    }

    @Override
    public CollectionDefinition getCollectionDefinition() {
        return collectionDefinition;
    }

    @Override
    public void setCollectionDefinition(CollectionDefinition collectionDefinition) {
        this.collectionDefinition = collectionDefinition;
    }

    @Override
    public String getSuppliedUrl() {
        return url;
    }

    @Override
    public String getSuppliedName() {
        return name;
    }

    @Override
    public String getSuppliedCreator() {
        return creator;
    }

    @Override
    public CollectionOrder getOrder() {
        return order;
    }

    @Override
    public void setOrder(CollectionOrder order) {
        this.order = order;
    }

    @Override
    public WeightsType getWeights() {
        return weights;
    }

    @Override
    public void setWeights(WeightsType weights) {
        this.weights = weights;
    }
    
    @Override
    public EntityType getCreator(EntityDefinitionManager entityDefinitionManager) {
        var playlist = dynamicPlaylist.get();
        SpotifyDataUtility.requireNonNull(playlist, "Internal Playlist is null, #populateSpotifyData must be invoked prior to getting API data");

        if (creatorType != null) {
            return creatorType;
        }

        var creator = playlist.getCreator();
        var artistEntity = entityDefinitionManager.lookup("User");
        return creatorType = artistEntity.createInstance(new StringTypeImpl(creator.getId()), new StringTypeImpl(creator.getName()));
    }

    @Override
    public Playlist getPlaylist() {
        var playlist = dynamicPlaylist.get();
        return SpotifyDataUtility.requireNonNull(playlist, "Internal Playlist is null, #populateSpotifyData must be invoked prior to getting API data");
    }

    @Override
    public boolean isSpotifyDataPopulated() {
        return dynamicPlaylist.isPopulated();
    }

    @Override
    public void populateSpotifyData(Playlist playlist) {
        dynamicPlaylist.put(playlist);
    }

    @Override
    public String stringValue() {
        if (collectionDefinition == CollectionDefinition.URL) {
            return String.format("collection(%s)", url);
        }

        if (collectionDefinition == CollectionDefinition.SONG_LIST) {
            return "collection(list-expression)";
        }

        return String.format("collection(\"%s\" by \"%s\")", name, creator);
    }

    @Override
    public QilletniType plusOperator(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "+");
    }

    @Override
    public void plusOperatorInPlace(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "+");
    }

    @Override
    public QilletniType minusOperator(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "-");
    }

    @Override
    public void minusOperatorInPlace(QilletniType qilletniType) {
        throw new UnsupportedOperatorException(this, qilletniType, "+");
    }

    @Override
    public QilletniTypeClass<CollectionType> getTypeClass() {
        return QilletniTypeClass.COLLECTION;
    }

    @Override
    public String toString() {
        if (collectionDefinition == CollectionDefinition.URL) {
            return "CollectionType{url='%s'}".formatted(url);
        }
        
        if (collectionDefinition == CollectionDefinition.SONG_LIST) {
            return "CollectionType{list-expression}";
        }

        return "CollectionType{title='%s', artist='%s'}".formatted(name, creator);
    }
}

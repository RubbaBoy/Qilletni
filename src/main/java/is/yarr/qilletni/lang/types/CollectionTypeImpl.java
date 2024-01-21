package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.SpotifyDataUtility;
import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.WeightsType;
import is.yarr.qilletni.api.lang.types.collection.CollectionDefinition;
import is.yarr.qilletni.api.lang.types.collection.CollectionOrder;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.api.music.Playlist;

public final class CollectionTypeImpl implements CollectionType {

    private CollectionDefinition collectionDefinition;
    private String url;
    private String name;
    private String creator;
    private CollectionOrder order = CollectionOrder.SEQUENTIAL;
    private WeightsType weights;
    private Playlist playlist;
    private EntityType creatorType;

    public CollectionTypeImpl(String url) {
        this.collectionDefinition = CollectionDefinition.URL;
        this.url = url;
    }

    public CollectionTypeImpl(String name, String creator) {
        this.collectionDefinition = CollectionDefinition.NAME_CREATOR;
        this.name = name;
        this.creator = creator;
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
        return SpotifyDataUtility.requireNonNull(playlist, "Internal Playlist is null, #populateSpotifyData must be invoked prior to getting API data");
    }

    @Override
    public boolean isSpotifyDataPopulated() {
        return playlist != null;
    }

    @Override
    public void populateSpotifyData(Playlist playlist) {
        this.playlist = playlist;
    }

    @Override
    public String stringValue() {
        if (collectionDefinition == CollectionDefinition.URL) {
            return String.format("collection(%s)", url);
        }

        return String.format("collection(\"%s\" by \"%s\")", name, creator);
    }

    @Override
    public QilletniTypeClass<CollectionType> getTypeClass() {
        return QilletniTypeClass.COLLECTION;
    }

    @Override
    public String toString() {
        if (collectionDefinition == CollectionDefinition.URL) {
            return "CollectionType{url='" + url + "'}";
        }

        return "CollectionType{title='" + name + "', artist='" + creator + "'}";
    }
}

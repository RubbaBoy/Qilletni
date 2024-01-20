package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.SpotifyDataUtility;
import is.yarr.qilletni.lang.types.collection.CollectionDefinition;
import is.yarr.qilletni.lang.types.collection.CollectionOrder;
import is.yarr.qilletni.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.music.Playlist;

public final class CollectionType extends QilletniType {

    private CollectionDefinition collectionDefinition;
    private String url;
    private String name;
    private String creator;
    private CollectionOrder order = CollectionOrder.SEQUENTIAL;
    private WeightsType weights;
    private Playlist playlist;
    private EntityType creatorType;

    public CollectionType(String url) {
        this.collectionDefinition = CollectionDefinition.URL;
        this.url = url;
    }

    public CollectionType(String name, String creator) {
        this.collectionDefinition = CollectionDefinition.NAME_CREATOR;
        this.name = name;
        this.creator = creator;
    }

    public CollectionDefinition getCollectionDefinition() {
        return collectionDefinition;
    }

    public void setCollectionDefinition(CollectionDefinition collectionDefinition) {
        this.collectionDefinition = collectionDefinition;
    }

    public String getSuppliedUrl() {
        return url;
    }

    public String getSuppliedName() {
        return name;
    }

    public String getSuppliedCreator() {
        return creator;
    }

    public CollectionOrder getOrder() {
        return order;
    }

    public void setOrder(CollectionOrder order) {
        this.order = order;
    }

    public WeightsType getWeights() {
        return weights;
    }

    public void setWeights(WeightsType weights) {
        this.weights = weights;
    }
    
    public EntityType getCreator(EntityDefinitionManager entityDefinitionManager) {
        SpotifyDataUtility.requireNonNull(playlist, "Internal Playlist is null, #populateSpotifyData must be invoked prior to getting API data");

        if (creatorType != null) {
            return creatorType;
        }

        var creator = playlist.getCreator();
        var artistEntity = entityDefinitionManager.lookup("User");
        return creatorType = artistEntity.createInstance(new StringType(creator.getId()), new StringType(creator.getName()));
    }

    public Playlist getPlaylist() {
        return SpotifyDataUtility.requireNonNull(playlist, "Internal Playlist is null, #populateSpotifyData must be invoked prior to getting API data");
    }

    public boolean isSpotifyDataPopulated() {
        return playlist != null;
    }

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

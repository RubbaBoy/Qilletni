package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.types.collection.CollectionDefinition;
import is.yarr.qilletni.api.lang.types.collection.CollectionOrder;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.api.music.Playlist;

public non-sealed interface CollectionType extends QilletniType {
    CollectionDefinition getCollectionDefinition();

    void setCollectionDefinition(CollectionDefinition collectionDefinition);

    String getSuppliedUrl();

    String getSuppliedName();

    String getSuppliedCreator();

    CollectionOrder getOrder();

    void setOrder(CollectionOrder order);

    WeightsType getWeights();

    void setWeights(WeightsType weights);

    EntityType getCreator(EntityDefinitionManager entityDefinitionManager);

    Playlist getPlaylist();

    boolean isSpotifyDataPopulated();

    void populateSpotifyData(Playlist playlist);
}

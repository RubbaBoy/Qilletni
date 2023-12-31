package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.types.collection.CollectionDefinition;
import is.yarr.qilletni.lang.types.collection.CollectionOrder;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

public final class CollectionType extends QilletniType {

    private CollectionDefinition collectionDefinition;
    private String url;
    private String name;
    private String creator;
    private CollectionOrder order = CollectionOrder.SEQUENTIAL;
    private WeightsType weights;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

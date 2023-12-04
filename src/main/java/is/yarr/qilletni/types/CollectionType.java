package is.yarr.qilletni.types;

import is.yarr.qilletni.types.collection.CollectionDefinition;

public final class CollectionType implements QilletniType {

    private CollectionDefinition collectionDefinition;
    private String url;
    private String name;
    private String creator;
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

    @Override
    public String stringValue() {
        if (collectionDefinition == CollectionDefinition.URL) {
            return String.format("collection(%s)", url);
        }

        return String.format("collection(\"%s\" by \"%s\")", name, creator);
    }

    @Override
    public String typeName() {
        return "collection";
    }

    @Override
    public String toString() {
        if (collectionDefinition == CollectionDefinition.URL) {
            return "CollectionType{url='" + url + "'}";
        }

        return "CollectionType{title='" + name + "', artist='" + creator + "'}";
    }
}

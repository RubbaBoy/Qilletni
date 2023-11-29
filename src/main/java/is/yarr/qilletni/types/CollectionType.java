package is.yarr.qilletni.types;

public final class CollectionType implements QilletniType {

    @Override
    public String stringValue() {
        return "~collection~";
    }

    @Override
    public String typeName() {
        return "collection";
    }
}

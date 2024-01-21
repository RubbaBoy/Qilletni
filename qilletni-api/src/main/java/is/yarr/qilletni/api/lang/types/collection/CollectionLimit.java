package is.yarr.qilletni.api.lang.types.collection;

public record CollectionLimit(int limitCount, CollectionLimitUnit limitUnit) {

    @Override
    public String toString() {
        return String.format("CollectionLimit{%d%s}", limitCount, limitUnit.getUnitText());
    }
}

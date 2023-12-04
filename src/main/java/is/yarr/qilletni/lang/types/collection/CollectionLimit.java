package is.yarr.qilletni.lang.types.collection;

public class CollectionLimit {
    
    private final int limitCount;
    private final CollectionLimitUnit limitUnit;

    public CollectionLimit(int limitCount, CollectionLimitUnit limitUnit) {
        this.limitCount = limitCount;
        this.limitUnit = limitUnit;
    }

    public int getLimitCount() {
        return limitCount;
    }

    public CollectionLimitUnit getLimitUnit() {
        return limitUnit;
    }

    @Override
    public String toString() {
        return String.format("CollectionLimit{%d%s}", limitCount, limitUnit.getUnitText());
    }
}

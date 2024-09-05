package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ListTypeImpl implements ListType {
    
    private List<QilletniType> items;
    private final QilletniTypeClass<ListType> listType;

    public ListTypeImpl(QilletniTypeClass<?> innerType, List<QilletniType> items) {
        this.items = items;
        this.listType = QilletniTypeClass.createListOfType(innerType);
    }
    
    public static ListType emptyList() {
        return new ListTypeImpl(QilletniTypeClass.ANY, Collections.emptyList());
    }
    
    @Override
    public QilletniTypeClass<?> getSubType() {
        return listType.getSubType();
    }

    @Override
    public List<QilletniType> getItems() {
        return items;
    }

    @Override
    public void setItems(List<QilletniType> items) {
        this.items = Collections.unmodifiableList(items);
    }

    @Override
    public ListType copy() {
        return new ListTypeImpl(listType.getSubType(), items);
    }

    @Override
    public String stringValue() {
        return String.format("[%s]", items.stream().map(QilletniType::stringValue).collect(Collectors.joining(", ")));
    }

    @Override
    public QilletniTypeClass<ListType> getTypeClass() {
        return listType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListTypeImpl listType1 = (ListTypeImpl) o;
        return Objects.equals(items, listType1.items) && Objects.equals(listType, listType1.listType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, listType);
    }

    @Override
    public String toString() {
        return "ListType{" +
                "items=" + items +
                ", listType=" + listType +
                '}';
    }
}

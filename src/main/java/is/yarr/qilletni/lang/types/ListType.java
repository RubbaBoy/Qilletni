package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public sealed class ListType extends QilletniType permits TypelessListType {
    
    private List<QilletniType> items;
    private final QilletniTypeClass<ListType> listType;

    public ListType(QilletniTypeClass<?> innerType, List<QilletniType> items) {
        this.items = items;
        this.listType = QilletniTypeClass.createListOfType(innerType);
    }

    public QilletniTypeClass<?> getSubType() {
        return listType.getSubType();
    }

    public List<QilletniType> getItems() {
        return items;
    }

    public void setItems(List<QilletniType> items) {
        this.items = Collections.unmodifiableList(items);
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
        ListType listType1 = (ListType) o;
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

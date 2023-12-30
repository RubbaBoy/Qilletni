package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

import java.util.List;

public final class ListType extends QilletniType {
    
    private List<QilletniType> items;
    private final QilletniTypeClass<ListType> listType;

    public ListType(QilletniTypeClass<ListType> innerType, List<QilletniType> items) {
        this.items = items;
        this.listType = QilletniTypeClass.createListOfType(innerType);
    }

    public QilletniTypeClass<?> getInnerType() {
        return listType.getSubType();
    }

    public List<QilletniType> getItems() {
        return items;
    }

    public void setItems(List<QilletniType> items) {
        this.items = items;
    }

    @Override
    public String stringValue() {
        return String.format("%s[]", listType.getSubType().getTypeName());
    }

    @Override
    public QilletniTypeClass<ListType> getTypeClass() {
        return listType;
    }
}

package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

import java.util.Collections;
import java.util.List;

public final class TypelessListType extends ListType {
    
    public TypelessListType() {
        super(null, Collections.emptyList());
    }

    @Override
    public QilletniTypeClass<?> getSubType() {
        throw new UnsupportedOperationException("No operations can be performed on a typeless list");
    }

    @Override
    public List<QilletniType> getItems() {
        throw new UnsupportedOperationException("No operations can be performed on a typeless list");
    }

    @Override
    public void setItems(List<QilletniType> items) {
        throw new UnsupportedOperationException("No operations can be performed on a typeless list");
    }

    @Override
    public String stringValue() {
        throw new UnsupportedOperationException("No operations can be performed on a typeless list");
    }

    @Override
    public QilletniTypeClass<ListType> getTypeClass() {
        throw new UnsupportedOperationException("No operations can be performed on a typeless list");
    }
}

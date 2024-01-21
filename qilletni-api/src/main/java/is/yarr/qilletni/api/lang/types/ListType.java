package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

import java.util.List;

public non-sealed interface ListType extends QilletniType {
    QilletniTypeClass<?> getSubType();

    List<QilletniType> getItems();

    void setItems(List<QilletniType> items);
}

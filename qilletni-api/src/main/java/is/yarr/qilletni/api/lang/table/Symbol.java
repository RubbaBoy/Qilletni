package is.yarr.qilletni.api.lang.table;

import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;

public interface Symbol<T extends QilletniType> {
    String getName();

    QilletniTypeClass<T> getType();

    T getValue();

    void setValue(T value);
}

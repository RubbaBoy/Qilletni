package is.yarr.qilletni.lang.types.entity;

import is.yarr.qilletni.lang.table.Scope;
import is.yarr.qilletni.lang.types.QilletniType;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public record EntityAttributes(Map<String, QilletniType> properties, Map<String, UninitializedType> constructorParams, List<Consumer<Scope>> entityFunctionPopulators) {
}

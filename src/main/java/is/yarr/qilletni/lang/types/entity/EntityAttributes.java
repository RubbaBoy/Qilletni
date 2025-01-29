package is.yarr.qilletni.lang.types.entity;

import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinition;
import is.yarr.qilletni.api.lang.types.entity.UninitializedType;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record EntityAttributes(Map<String, Supplier<QilletniType>> properties, Map<String, UninitializedType> constructorParams, List<FunctionPopulator> entityFunctionPopulators) {
}

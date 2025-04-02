package dev.qilletni.impl.lang.types.entity;

import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.lang.types.entity.UninitializedType;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public record EntityAttributes(Map<String, Supplier<QilletniType>> properties, Map<String, UninitializedType> constructorParams, List<FunctionPopulator> entityFunctionPopulators) {
}

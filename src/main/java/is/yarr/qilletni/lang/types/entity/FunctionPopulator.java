package is.yarr.qilletni.lang.types.entity;

import is.yarr.qilletni.api.lang.table.Scope;

import java.util.function.Consumer;

public record FunctionPopulator(boolean isStaticFunction, Consumer<Scope> functionPopulator) {}

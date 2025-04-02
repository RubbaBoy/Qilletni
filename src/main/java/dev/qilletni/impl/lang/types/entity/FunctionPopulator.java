package dev.qilletni.impl.lang.types.entity;

import dev.qilletni.api.lang.table.Scope;

import java.util.function.Consumer;

public record FunctionPopulator(boolean isStaticFunction, Consumer<Scope> functionPopulator) {}

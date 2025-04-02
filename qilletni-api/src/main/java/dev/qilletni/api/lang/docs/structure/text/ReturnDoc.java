package dev.qilletni.api.lang.docs.structure.text;

import dev.qilletni.api.lang.docs.structure.DocFieldType;
import org.jetbrains.annotations.Nullable;

public record ReturnDoc(@Nullable DocFieldType docFieldType, @Nullable DocDescription description) {
}

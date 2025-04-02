package dev.qilletni.api.lang.docs.structure.text.inner;

import dev.qilletni.api.lang.docs.structure.DocFieldType;
import dev.qilletni.api.lang.docs.structure.text.DocDescription;
import org.jetbrains.annotations.Nullable;

public record FieldDoc(@Nullable DocDescription description, @Nullable DocFieldType fieldType) implements InnerDoc {
}

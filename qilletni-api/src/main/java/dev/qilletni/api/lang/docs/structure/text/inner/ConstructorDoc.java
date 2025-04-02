package dev.qilletni.api.lang.docs.structure.text.inner;

import dev.qilletni.api.lang.docs.structure.text.DocDescription;
import dev.qilletni.api.lang.docs.structure.text.ParamDoc;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ConstructorDoc(@Nullable DocDescription description, List<ParamDoc> paramDocs) implements InnerDoc {
}

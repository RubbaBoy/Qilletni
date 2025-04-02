package dev.qilletni.api.lang.docs.structure.text.inner;

import dev.qilletni.api.lang.docs.structure.text.DocDescription;
import dev.qilletni.api.lang.docs.structure.text.DocErrors;
import dev.qilletni.api.lang.docs.structure.text.DocOnLine;
import dev.qilletni.api.lang.docs.structure.text.ParamDoc;
import dev.qilletni.api.lang.docs.structure.text.ReturnDoc;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record FunctionDoc(@Nullable DocDescription description, List<ParamDoc> paramDocs, @Nullable ReturnDoc returnDoc, @Nullable DocOnLine docOnLine, @Nullable DocErrors docErrors) implements InnerDoc {
}

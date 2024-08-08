package is.yarr.qilletni.api.lang.docs.structure.text.inner;

import is.yarr.qilletni.api.lang.docs.structure.text.DocDescription;
import is.yarr.qilletni.api.lang.docs.structure.text.ParamDoc;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ConstructorDoc(@Nullable DocDescription description, List<ParamDoc> paramDocs) implements InnerDoc {
}

package is.yarr.qilletni.api.lang.docs.structure.text.inner;

import is.yarr.qilletni.api.lang.docs.structure.DocFieldType;
import is.yarr.qilletni.api.lang.docs.structure.text.DocDescription;

public record FieldDoc(DocDescription description, DocFieldType fieldType) implements InnerDoc {
}

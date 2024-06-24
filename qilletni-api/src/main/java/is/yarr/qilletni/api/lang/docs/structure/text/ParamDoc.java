package is.yarr.qilletni.api.lang.docs.structure.text;

import is.yarr.qilletni.api.lang.docs.structure.DocFieldType;

public record ParamDoc(String name, DocFieldType docFieldType, DocDescription description) {
}

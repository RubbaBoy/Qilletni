package is.yarr.qilletni.api.lang.docs.structure.text.inner;

import is.yarr.qilletni.api.lang.docs.structure.text.DocDescription;
import is.yarr.qilletni.api.lang.docs.structure.text.DocErrors;
import is.yarr.qilletni.api.lang.docs.structure.text.DocOnLine;
import is.yarr.qilletni.api.lang.docs.structure.text.ParamDoc;
import is.yarr.qilletni.api.lang.docs.structure.text.ReturnDoc;

import java.util.List;

public record FunctionDoc(DocDescription description, List<ParamDoc> paramDocs, ReturnDoc returnDoc, DocOnLine docOnLine, DocErrors docErrors) implements InnerDoc {
}

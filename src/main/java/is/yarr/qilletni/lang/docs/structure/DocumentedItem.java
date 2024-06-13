package is.yarr.qilletni.lang.docs.structure;

import is.yarr.qilletni.lang.docs.structure.item.DocumentedType;
import is.yarr.qilletni.lang.docs.structure.text.inner.InnerDoc;

/**
 * @param itemBeingDocumented The item actually being documented
 * @param innerDoc             The structured text of the doc string
 */
public record DocumentedItem(DocumentedType itemBeingDocumented, InnerDoc innerDoc) {
}

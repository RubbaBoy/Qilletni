package dev.qilletni.api.lang.docs.structure;

import dev.qilletni.api.lang.docs.structure.item.DocumentedType;
import dev.qilletni.api.lang.docs.structure.text.inner.InnerDoc;

/**
 * @param itemBeingDocumented The item actually being documented
 * @param innerDoc             The structured text of the doc string
 */
public record DocumentedItem(DocumentedType itemBeingDocumented, InnerDoc innerDoc) {
}

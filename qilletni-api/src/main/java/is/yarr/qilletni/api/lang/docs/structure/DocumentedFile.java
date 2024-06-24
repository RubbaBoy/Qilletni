package is.yarr.qilletni.api.lang.docs.structure;

import java.util.List;

public record DocumentedFile(String fileName, List<DocumentedItem> documentedItems) {
}

package dev.qilletni.api.lang.docs.structure;

import java.nio.file.Path;
import java.util.List;

public record DocumentedFile(String fileName, Path importPath, List<DocumentedItem> documentedItems) {
}

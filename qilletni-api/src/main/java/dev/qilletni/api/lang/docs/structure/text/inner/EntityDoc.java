package dev.qilletni.api.lang.docs.structure.text.inner;

import dev.qilletni.api.lang.docs.structure.DocumentedItem;
import dev.qilletni.api.lang.docs.structure.item.DocumentedTypeFunction;
import dev.qilletni.api.lang.docs.structure.text.DocDescription;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @param description          The description of the actual entity definition
 * @param containedItems       Fields, functions, constructors, etc. of the entity
 * @param onExtensionFunctions Functions that are added onto the entity from an 'on' extension from either the current
 *                             library or an external one
 */
public record EntityDoc(@Nullable DocDescription description, List<DocumentedItem> containedItems,
                        List<DocumentedItem> onExtensionFunctions) implements InnerDoc {
    public EntityDoc(@Nullable DocDescription description) {
        this(description, new ArrayList<>(), new ArrayList<>());
    }

    public void addDocItem(DocumentedItem documentedItem) {
        containedItems.add(documentedItem);
    }

    public void addOnExtension(DocumentedItem documentedItem) {
        if (!(documentedItem.itemBeingDocumented() instanceof DocumentedTypeFunction)) {
            throw new IllegalArgumentException("Can only add functions to an entity's on extension");
        }
        
        onExtensionFunctions.add(documentedItem);
    }
}

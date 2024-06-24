package is.yarr.qilletni.api.lang.docs.structure.text.inner;

import is.yarr.qilletni.api.lang.docs.structure.DocumentedItem;
import is.yarr.qilletni.api.lang.docs.structure.text.DocDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @param description The description of the actual entity definition
 * @param innerDocs   Fields, functions, constructors, etc. of the entity
 */
public record EntityDoc(DocDescription description, List<DocumentedItem> containedItems) implements InnerDoc {
    public EntityDoc(DocDescription description) {
        this(description, new ArrayList<>());
    }
    
    public void addDocItem(DocumentedItem documentedItem) {
        containedItems.add(documentedItem);
    }
}

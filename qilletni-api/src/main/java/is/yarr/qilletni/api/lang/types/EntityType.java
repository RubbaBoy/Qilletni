package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinition;

public non-sealed interface EntityType extends AnyType {
    EntityDefinition getEntityDefinition();

    Scope getEntityScope();
    
    void validateType(String typeName);
}

package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinition;

public non-sealed interface EntityType extends QilletniType {
    EntityDefinition getEntityDefinition();

    Scope getEntityScope();
}

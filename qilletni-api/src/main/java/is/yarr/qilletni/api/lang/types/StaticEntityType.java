package is.yarr.qilletni.api.lang.types;

import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinition;

public non-sealed interface StaticEntityType extends QilletniType {
    
    EntityDefinition getEntityDefinition();

    /**
     * Holds only the static functions of the entity.
     * 
     * @return The {@link Scope} containing the static functions of the entity
     */
    Scope getEntityScope();
    
}

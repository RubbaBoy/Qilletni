package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.table.Scope;
import is.yarr.qilletni.lang.types.entity.EntityDefinition;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

import java.util.stream.Collectors;

public final class EntityType extends QilletniType {
    
    private final Scope entityScope;
    private final EntityDefinition entityDefinition;

    public EntityType(Scope entityScope, EntityDefinition entityDefinition) {
        this.entityScope = entityScope;
        this.entityDefinition = entityDefinition;
    }

    public EntityDefinition getEntityDefinition() {
        return entityDefinition;
    }

    public Scope getEntityScope() {
        return entityScope;
    }

    @Override
    public String stringValue() {
        var entitySymbols = entityScope.getAllSymbols(); 
        var propertyString = entitySymbols.keySet()
                .stream()
                .map(propertyName -> String.format("%s = %s", propertyName, entitySymbols.get(propertyName).getValue().stringValue()))
                .collect(Collectors.joining(", "));
        return String.format("%s(%s)", entityDefinition.getTypeName(), propertyString);
    }

    @Override
    public String typeName() {
        return entityDefinition.getTypeName();
    }

    @Override
    public QilletniTypeClass<EntityType> getTypeClass() {
        return entityDefinition.getQilletniTypeClass();
    }

    @Override
    public String toString() {
        return "EntityType{" +
                ", entityScope=" + entityScope +
                ", entityDefinition=" + entityDefinition +
                '}';
    }
}

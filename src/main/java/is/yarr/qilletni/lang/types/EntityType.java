package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.lang.table.Scope;
import is.yarr.qilletni.lang.types.entity.EntityDefinition;
import is.yarr.qilletni.lang.types.typeclass.QilletniTypeClass;

import java.util.Objects;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityType that = (EntityType) o;
        return Objects.equals(entityScope, that.entityScope) && Objects.equals(entityDefinition, that.entityDefinition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityScope, entityDefinition);
    }

    @Override
    public String toString() {
        return "EntityType{" +
                ", entityScope=" + entityScope +
                ", entityDefinition=" + entityDefinition +
                '}';
    }
}

package is.yarr.qilletni.lang.types;

import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.table.Scope;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.lang.types.entity.EntityDefinitionImpl;

import java.util.Objects;
import java.util.stream.Collectors;

public final class EntityTypeImpl implements EntityType {
    
    private final Scope entityScope;
    private final EntityDefinitionImpl entityDefinition;

    public EntityTypeImpl(Scope entityScope, EntityDefinitionImpl entityDefinition) {
        this.entityScope = entityScope;
        this.entityDefinition = entityDefinition;
    }

    @Override
    public EntityDefinitionImpl getEntityDefinition() {
        return entityDefinition;
    }

    @Override
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
        if (!(o instanceof EntityTypeImpl that)) return false;
        return Objects.equals(entityScope.getAllSymbols(), that.entityScope.getAllSymbols()) && Objects.equals(entityDefinition, that.entityDefinition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityScope.getAllSymbols(), entityDefinition.getTypeName());
    }

    @Override
    public String toString() {
        return stringValue();
    }
}

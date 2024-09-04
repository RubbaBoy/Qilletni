package is.yarr.qilletni.lang.types.entity;

import is.yarr.qilletni.api.lang.types.EntityType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinition;
import is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager;
import is.yarr.qilletni.api.lang.types.entity.EntityInitializer;
import is.yarr.qilletni.lang.exceptions.NoTypeAdapterException;
import is.yarr.qilletni.lang.internal.adapter.TypeAdapterRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EntityInitializerImpl implements EntityInitializer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityInitializerImpl.class);

    private final TypeAdapterRegistrar typeAdapterRegistrar;
    private final EntityDefinitionManager entityDefinitionManager;

    public EntityInitializerImpl(TypeAdapterRegistrar typeAdapterRegistrar, EntityDefinitionManager entityDefinitionManager) {
        this.typeAdapterRegistrar = typeAdapterRegistrar;
        this.entityDefinitionManager = entityDefinitionManager;
    }

    @Override
    public EntityType initializeEntity(String entityName, Object... args) {
        var entityDefinition = entityDefinitionManager.lookup(entityName);
        return initializeEntity(entityDefinition, args);
    }

    @Override
    public EntityType initializeEntity(String entityName, List<Object> args) {
        var entityDefinition = entityDefinitionManager.lookup(entityName);
        return initializeEntity(entityDefinition, args);
    }

    @Override
    public EntityType initializeEntity(EntityDefinition entityDefinition, Object... args) {
        return initializeEntity(entityDefinition, List.of(args));
    }

    @Override
    public EntityType initializeEntity(EntityDefinition entityDefinition, List<Object> args) {
        var adaptedArgs = new ArrayList<QilletniType>();

        for (var arg : args) {
            if (arg instanceof QilletniType qilletniType) {
                adaptedArgs.add(qilletniType);
                continue;
            }
            
            var typeAdapter = typeAdapterRegistrar.findAnyTypeAdapter(arg.getClass())
                    .orElseThrow(() -> new NoTypeAdapterException(arg.getClass()));

            adaptedArgs.add(typeAdapter.convertCastedType(arg));
        }
        
        return entityDefinition.createInstance(adaptedArgs);
    }
}

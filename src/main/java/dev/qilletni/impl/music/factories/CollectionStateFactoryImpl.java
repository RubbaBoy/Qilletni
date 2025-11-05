package dev.qilletni.impl.music.factories;

import dev.qilletni.api.lang.types.CollectionType;
import dev.qilletni.api.music.factories.CollectionStateFactory;
import dev.qilletni.api.music.orchestration.CollectionState;
import dev.qilletni.api.music.supplier.DynamicProvider;
import dev.qilletni.impl.music.orchestration.CollectionStateImpl;

public class CollectionStateFactoryImpl implements CollectionStateFactory {

    private final DynamicProvider dynamicProvider;

    public CollectionStateFactoryImpl(DynamicProvider dynamicProvider) {
        this.dynamicProvider = dynamicProvider;
    }

    @Override
    public CollectionState createFromCollection(CollectionType collection) {
        return new CollectionStateImpl(collection, dynamicProvider);
    }
}

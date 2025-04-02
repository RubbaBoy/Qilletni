package dev.qilletni.api.music.supplier;

import dev.qilletni.api.auth.ServiceProvider;
import dev.qilletni.api.lib.persistence.PackageConfig;
import dev.qilletni.api.music.MusicCache;
import dev.qilletni.api.music.MusicFetcher;
import dev.qilletni.api.music.StringIdentifier;
import dev.qilletni.api.music.factories.AlbumTypeFactory;
import dev.qilletni.api.music.factories.CollectionTypeFactory;
import dev.qilletni.api.music.factories.SongTypeFactory;
import dev.qilletni.api.music.orchestration.TrackOrchestrator;

public interface DynamicProvider {

    void addServiceProvider(ServiceProvider serviceProvider);

    void switchProvider(String providerName);
    
    void initializeInitialProvider(PackageConfig internalPackageConfig);

    ServiceProvider getProvider(String providerName);

    ServiceProvider getCurrentProvider();

    void initFactories(SongTypeFactory songTypeFactory, CollectionTypeFactory collectionTypeFactory, AlbumTypeFactory albumTypeFactory);
    
    void shutdownProviders();

    MusicCache getMusicCache();

    MusicFetcher getMusicFetcher();
    
    TrackOrchestrator getTrackOrchestrator();
    
    StringIdentifier getStringIdentifier();
}

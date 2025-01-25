package is.yarr.qilletni.api.music.supplier;

import is.yarr.qilletni.api.auth.ServiceProvider;
import is.yarr.qilletni.api.music.MusicCache;
import is.yarr.qilletni.api.music.MusicFetcher;
import is.yarr.qilletni.api.music.StringIdentifier;
import is.yarr.qilletni.api.music.factories.AlbumTypeFactory;
import is.yarr.qilletni.api.music.factories.CollectionTypeFactory;
import is.yarr.qilletni.api.music.factories.SongTypeFactory;
import is.yarr.qilletni.api.music.orchestration.TrackOrchestrator;

public interface DynamicProvider {

    void addServiceProvider(ServiceProvider serviceProvider);

    void switchProvider(String providerName);

    ServiceProvider getProvider(String providerName);

    ServiceProvider getCurrentProvider();

    void initFactories(SongTypeFactory songTypeFactory, CollectionTypeFactory collectionTypeFactory, AlbumTypeFactory albumTypeFactory);
    
    void shutdownProviders();

    MusicCache getMusicCache();

    MusicFetcher getMusicFetcher();
    
    TrackOrchestrator getTrackOrchestrator();
    
    StringIdentifier getStringIdentifier();
}

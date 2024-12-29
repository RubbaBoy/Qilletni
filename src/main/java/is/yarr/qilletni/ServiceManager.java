package is.yarr.qilletni;

import is.yarr.qilletni.api.auth.ServiceProvider;
import is.yarr.qilletni.api.lib.qll.QllInfo;
import is.yarr.qilletni.api.music.supplier.DynamicProvider;
import is.yarr.qilletni.lib.persistence.PackageConfigImpl;
import is.yarr.qilletni.music.orchestration.DefaultTrackOrchestrator;
import is.yarr.qilletni.music.supplier.DynamicProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ServiceManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceManager.class);
    
    public static DynamicProvider createDynamicProvider(List<QllInfo> qllInfos) {
        var providers = qllInfos.stream()
                .map(QllInfo::providerClass)
                .filter(Objects::nonNull)
                .map(ServiceManager::loadServiceProviderClass)
                .filter(Optional::isPresent).map(Optional::get).toList();

        if (providers.isEmpty()) {
            throw new RuntimeException("No service providers found!");
        }
        
        var dynamicProvider = new DynamicProviderImpl();
        
        for (var provider : providers) {
            // Pass in unloaded PackageConfig to the provider. They don't have to load it (or create one) if they don't need to.
            var packageConfig = PackageConfigImpl.createPackageConfig(provider.getName());
            provider.initialize(DefaultTrackOrchestrator::new, packageConfig).join();
            
            dynamicProvider.addServiceProvider(provider);
        }

        var defaultProvider = providers.getFirst().getName();
        if (providers.size() != 1) {
            LOGGER.info("Choosing {} as a default provider", defaultProvider);
        }
        
        dynamicProvider.switchProvider(defaultProvider);
        
        return dynamicProvider;
    }
    
    private static Optional<ServiceProvider> loadServiceProviderClass(String className) {
        try {
            LOGGER.debug("Loading service provider: {}", className);
            return Optional.of((ServiceProvider) Thread.currentThread().getContextClassLoader().loadClass(className).getConstructor().newInstance());
        } catch (Exception e) {
            LOGGER.error("An exception occurred while loading service provider: " + className, e);
            return Optional.empty();
        }
    }
    
}

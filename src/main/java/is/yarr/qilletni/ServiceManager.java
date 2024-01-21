package is.yarr.qilletni;

import is.yarr.qilletni.api.auth.ServiceProvider;

import java.util.ServiceLoader;

public class ServiceManager {
    
    public static ServiceProvider findServiceProvider() {
        var serviceLoader = ServiceLoader.load(ServiceProvider.class);
        return serviceLoader.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No service providers found!"))
                .get();
    }
    
}

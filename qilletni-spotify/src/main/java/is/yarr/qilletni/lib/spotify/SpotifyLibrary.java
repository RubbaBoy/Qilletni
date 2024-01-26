package is.yarr.qilletni.lib.spotify;

import is.yarr.qilletni.api.lang.internal.NativeFunctionClassInjector;
import is.yarr.qilletni.api.lib.Library;
import is.yarr.qilletni.music.spotify.auth.SpotifyApiSingleton;
import is.yarr.qilletni.music.spotify.creator.PlaylistCreator;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class SpotifyLibrary implements Library {
    
    @Override
    public String getName() {
        return "Spotify";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public List<Class<?>> getNativeClasses() {
        return List.of(PlayRedirect.class, PlaylistToolsFunctions.class);
    }

    @Override
    public String getImportName() {
        return "spotify";
    }

    @Override
    public Optional<InputStream> readPath(String path) {
        return Optional.ofNullable(getClass().getResourceAsStream("/" + path));
    }

    @Override
    public void supplyNativeFunctionBindings(NativeFunctionClassInjector nativeFunctionClassInjector) {
        nativeFunctionClassInjector.addInjectableInstance(new PlaylistCreator(SpotifyApiSingleton.getSpotifyAuthorizer()));
    }
}

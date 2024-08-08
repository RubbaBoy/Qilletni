package is.yarr.qilletni.lib.spotify;

import is.yarr.qilletni.api.lang.internal.NativeFunctionClassInjector;
import is.yarr.qilletni.api.lib.NativeFunctionBindingFactory;
import is.yarr.qilletni.music.spotify.auth.SpotifyApiSingleton;
import is.yarr.qilletni.music.spotify.creator.PlaylistCreator;

public class SpotifyNativeFunctionBindingFactory implements NativeFunctionBindingFactory {
    
    @Override
    public void applyNativeFunctionBindings(NativeFunctionClassInjector nativeFunctionClassInjector) {
        nativeFunctionClassInjector.addInjectableInstance(new PlaylistCreator(SpotifyApiSingleton.getSpotifyAuthorizer()));
    }
}

package dev.qilletni.lib.spotify;

import dev.qilletni.api.lang.internal.NativeFunctionClassInjector;
import dev.qilletni.api.lib.NativeFunctionBindingFactory;
import dev.qilletni.lib.spotify.music.auth.SpotifyApiSingleton;
import dev.qilletni.lib.spotify.music.creator.PlaylistCreator;

public class SpotifyNativeFunctionBindingFactory implements NativeFunctionBindingFactory {
    
    @Override
    public void applyNativeFunctionBindings(NativeFunctionClassInjector nativeFunctionClassInjector) {
        nativeFunctionClassInjector.addInjectableInstance(new PlaylistCreator(SpotifyApiSingleton.getSpotifyAuthorizer()));
    }
}

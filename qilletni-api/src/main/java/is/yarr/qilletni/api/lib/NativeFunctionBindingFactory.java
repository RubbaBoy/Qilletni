package is.yarr.qilletni.api.lib;

import is.yarr.qilletni.api.lang.internal.NativeFunctionClassInjector;

public interface NativeFunctionBindingFactory {
    
    /**
     * Applies any injectable native function classes to be used in the library.
     * 
     * @param nativeFunctionClassInjector the {@link NativeFunctionClassInjector} to apply the native function bindings to
     */
    void applyNativeFunctionBindings(NativeFunctionClassInjector nativeFunctionClassInjector);
    
}

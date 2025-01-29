package is.yarr.qilletni.api.lib;

import is.yarr.qilletni.api.lang.internal.NativeFunctionClassInjector;

/**
 * A factory that allows injection of custom instances in native methods.
 * 
 * @see <a href="https://qilletni.yarr.is/native_binding/native_bind_factories/">Native Bind Factories</a> in Qilletni docs
 */
public interface NativeFunctionBindingFactory {
    
    /**
     * Applies any injectable native function classes to be used in the library.
     * 
     * @param nativeFunctionClassInjector the {@link NativeFunctionClassInjector} to apply the native function bindings to
     */
    void applyNativeFunctionBindings(NativeFunctionClassInjector nativeFunctionClassInjector);
    
}

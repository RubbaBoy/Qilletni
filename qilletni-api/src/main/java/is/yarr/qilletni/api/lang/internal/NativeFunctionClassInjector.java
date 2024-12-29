package is.yarr.qilletni.api.lang.internal;

import java.util.List;

/**
 * This interface is used to inject classes into the constructors of the parent classes of native methods.
 */
public interface NativeFunctionClassInjector {

    /**
     * Add an instance of anything to be injected into a constructor, by knowing its superclass.
     * 
     * @param object The instance to inject
     */
    void addInjectableInstance(Object object);
    
    /**
     * Add an instance of anything to be injected into a constructor, by knowing its superclass. Only classes in the
     * permittedClasses list will be injected. If the class is not included in the list, it will act as if it does not
     * exist and may cause an error.
     * 
     * @param object The instance to inject
     * @param permittedClasses The classes that are allowed to be injected
     */
    void addScopedInjectableInstance(Object object, List<Class<?>> permittedClasses);
    
}

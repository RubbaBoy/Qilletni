package is.yarr.qilletni.api.lang.internal;

import java.util.List;

/**
 * This interface is used to inject classes into the constructors of the parent classes of native methods.
 * <br><br>
 * The following classes are automatically injected to be used with any library:<br>
 * <ul>
 *     <li>{@link is.yarr.qilletni.api.music.MusicPopulator}</li>
 *     <li>{@link is.yarr.qilletni.api.lang.types.entity.EntityDefinitionManager}</li>
 *     <li>{@link is.yarr.qilletni.api.lang.types.entity.EntityInitializer}</li>
 *     <li>{@link is.yarr.qilletni.api.lang.types.list.ListInitializer}</li>
 *     <li>{@link is.yarr.qilletni.api.music.factories.SongTypeFactory}</li>
 *     <li>{@link is.yarr.qilletni.api.music.factories.CollectionTypeFactory}</li>
 *     <li>{@link is.yarr.qilletni.api.music.factories.AlbumTypeFactory}</li>
 *     <li>{@link FunctionInvoker}</li>
 *     <li>{@link is.yarr.qilletni.api.lang.types.conversion.TypeConverter}</li>
 *     <li>{@link is.yarr.qilletni.api.music.supplier.DynamicProvider}</li>
 *     <li>{@link is.yarr.qilletni.api.lib.persistence.PackageConfig}</li>
 * </ul>
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
    
    /**
     * Add an instance of anything to be injected into a constructor, by knowing its superclass. Only classes with a
     * name in the permittedClassNames list will be injected. If the class is not included in the list, it will act as
     * if it does not exist and may cause an error.
     * 
     * @param object The instance to inject
     * @param permittedClassNames The class names that are allowed to be injected
     */
    void addScopedInjectableInstanceByNames(Object object, List<String> permittedClassNames);
    
}

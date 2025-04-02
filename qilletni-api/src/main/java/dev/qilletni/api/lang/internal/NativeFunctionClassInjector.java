package dev.qilletni.api.lang.internal;

import dev.qilletni.api.lang.types.conversion.TypeConverter;
import dev.qilletni.api.lang.types.entity.EntityDefinitionManager;
import dev.qilletni.api.lang.types.entity.EntityInitializer;
import dev.qilletni.api.lang.types.list.ListInitializer;
import dev.qilletni.api.lib.persistence.PackageConfig;
import dev.qilletni.api.music.MusicPopulator;
import dev.qilletni.api.music.factories.AlbumTypeFactory;
import dev.qilletni.api.music.factories.CollectionTypeFactory;
import dev.qilletni.api.music.factories.SongTypeFactory;
import dev.qilletni.api.music.supplier.DynamicProvider;

import java.util.List;

/**
 * This interface is used to inject classes into the constructors of the parent classes of native methods.
 * <br><br>
 * The following classes are automatically injected to be used with any library:<br>
 * <ul>
 *     <li>{@link MusicPopulator}</li>
 *     <li>{@link EntityDefinitionManager}</li>
 *     <li>{@link EntityInitializer}</li>
 *     <li>{@link ListInitializer}</li>
 *     <li>{@link SongTypeFactory}</li>
 *     <li>{@link CollectionTypeFactory}</li>
 *     <li>{@link AlbumTypeFactory}</li>
 *     <li>{@link FunctionInvoker}</li>
 *     <li>{@link TypeConverter}</li>
 *     <li>{@link DynamicProvider}</li>
 *     <li>{@link PackageConfig}</li>
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

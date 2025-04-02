package dev.qilletni.api.lib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation placed on methods or classes to specify that the method (or containing methods, if on a class) are
 * extension methods on a specific Qilletni type.
 * @see <a href="https://qilletni.dev/native_binding/native_functions/#native-entity-functions">Native Entity Functions</a> in Qilletni docs
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NativeOn {

    /**
     * The name of the type that this method is an extension method on. This may either be a native type or an entity
     * name.
     * 
     * @return The name of the type this is an extension method on
     */
    String value();
}

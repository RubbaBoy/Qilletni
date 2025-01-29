package is.yarr.qilletni.api.lib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation placed on methods holding native classes that are bound to Qilletni methods. After a class is
 * initialized on a native method invocation, a method with this annotation will be called. This method is in the same
 * instance as what the native method will be invoked in, so this may set up instance variables in the class to be used
 * in that single invocation.
 * @see <a href="https://qilletni.yarr.is/native_binding/native_functions/#preload-methods">Preload Methods</a> in Qilletni docs
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface BeforeAnyInvocation {
}

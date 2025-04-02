package dev.qilletni.api.lib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation placed on methods to specify that the return type of the method should not be adapted to a Qilletni
 * type.
 * 
 * @see <a href="https://qilletni.dev/native_binding/native_functions/#skipping-automatic-type-conversion">Skipping Automatic Type Conversion</a> in Qilletni docs
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SkipReturnTypeAdapter {
}

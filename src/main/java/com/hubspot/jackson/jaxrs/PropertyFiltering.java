package com.hubspot.jackson.jaxrs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyFiltering {
  String using() default "property";
  String[] always() default {};

  /**
   * Prefix added to all requested property names. This does not apply to attributes specified with {@link #always()}
   */
  String prefix() default "";
}

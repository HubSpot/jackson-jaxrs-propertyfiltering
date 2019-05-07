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
   * Prefix added to all requested properties. If this is a full attribute name, be sure to end it with a period. This does not apply to attributes specified with {@link #always()}
   */
  String prefix() default "";
}

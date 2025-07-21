package com.siewe_rostand.tvcam.common.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.siewe_rostand.tvcam.common.constraints.validator.ExistsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Constraint(validatedBy = ExistsValidator.class)
@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
public @interface Exists {
  String message() default "{constraints.exists}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String property();

  String repository();

  @Target({TYPE, FIELD, ANNOTATION_TYPE})
  @Retention(RUNTIME)
  @Documented
  @interface List {
    Exists[] value();
  }
}

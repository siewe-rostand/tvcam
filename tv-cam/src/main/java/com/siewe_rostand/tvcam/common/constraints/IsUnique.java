package com.siewe_rostand.tvcam.common.constraints;

import com.siewe_rostand.tvcam.common.constraints.validator.IsUniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = IsUniqueValidator.class)
@Target({
        TYPE, FIELD,
        ANNOTATION_TYPE
})
@Retention(RUNTIME)
@Documented
public @interface IsUnique {
    String message() default "constraint.is-unique";
    Class <?> [] groups() default {};
    Class <? extends Payload> [] payload() default {};
    String property();
    String repository();
    IsUniqueValidator.UpdateAction action() default IsUniqueValidator.UpdateAction.INSERT;


    @Target({
            TYPE, FIELD,
            ANNOTATION_TYPE
    })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        IsUnique[] value();
    }
}

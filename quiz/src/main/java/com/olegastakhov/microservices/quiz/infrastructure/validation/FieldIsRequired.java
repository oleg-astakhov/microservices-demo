package com.olegastakhov.microservices.quiz.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FieldIsRequiredValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldIsRequired {

    String i18nFieldKey();
    String i18nMessageKey() default "validationMessage.fieldIsRequired";
    String message() default "Field is required";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
